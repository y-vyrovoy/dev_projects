#include "stdafx.h"
#include "ResponseDispatcher.h"

#include <sstream>
#include <exception>

#include "Logger.h"
#include "MessageException.h"

RequestIdType ResponseDispatcher::m_nextRequestID;

ResponseDispatcher::ResponseDispatcher() 
	: m_bForceStop{false}
{
}

RequestIdType ResponseDispatcher::registerRequest( SOCKET sock, RequestPtr request )
{
    RequestIdType id = m_nextRequestID++;

    std::unique_lock<std::mutex> lck( m_requestMutex );

	request->id = id;

    // mapping request with socket
    m_requestId2SocketMap[id] = sock;

	// adding to requests map and waiting queue
	m_requests[id] = std::move( request );
	m_requestWaitSentQueue.push( id );

    // updating response chain
    m_requestsChains[sock].push_back( id );

	m_cvRequest.notify_all();

	return id;
}

// METHOD IS NOT THREAD SAFE
RequestData * ResponseDispatcher::syncGetAndPumpTopRequest()
{
	if ( m_requestWaitSentQueue.isWaitingEmpty() )
	{
		return nullptr;
	}

	RequestIdType id = m_requestWaitSentQueue.moveNextToSent();

	auto it = m_requests.find( id );
	if ( it == m_requests.end() )
	{
		THROW_MESSAGE << "Failed to find request id #" << id << " while waiting queue contains it";
	}

	return it->second.get();
}


/// THREAD SAFE.
/// Moving top request from Waiting to Sent map and returninig its raw pointer.
/// If no requests are available waits until request will come
RequestData * ResponseDispatcher::scheduleNextRequest()
{
	std::unique_lock<std::mutex> lock( m_requestMutex );
	m_cvRequest.wait( lock, [this] () {return !m_requestWaitSentQueue.waitingEmpty() || m_bForceStop; } );

    if ( m_bForceStop )
    {
		DEBUG_LOG << " Throwing cTerminationException";
        throw cTerminationException();
    }

	return syncGetAndPumpTopRequest();
}


/// THREAD SAFE.
void ResponseDispatcher::rescheduleRequest( RequestIdType id )
{
	std::unique_lock<std::mutex> lck( m_requestMutex );

	m_requestWaitSentQueue.moveToWaiting( id );	
}

/// private. NOT THREAD SAFE
void ResponseDispatcher::syncRemoveRequestFromChain( RequestIdType id )
{
    auto itID = m_requestId2SocketMap.find( id );
    if ( itID == m_requestId2SocketMap.end() )
    {
        WARNING_LOG << "Can't find socket for the response id #" << id;
		return;
    }

    SOCKET sock = itID->second;

    auto itSock = m_requestsChains.find( sock );
    if ( itSock == m_requestsChains.end() )
    {
        WARNING_LOG << "Can't find chain for socket #" << sock;
		return;
    }

    itSock->second.remove( id );
}

enRequestState ResponseDispatcher::isRequestSent( RequestIdType id )
{
	if ( m_requestWaitSentQueue.isWaiting( id ) )
	{
		return enRequestState::REQ_WAITS;
	} 
	else if ( m_requestWaitSentQueue.isSent( id ) )
	{
		return enRequestState::REQ_SENT;
	}

	return enRequestState::REQ_UNKNOWN;
}



void ResponseDispatcher::registerResponse( ResponsePtr response )
{
    RequestIdType id = response->id;

	std::unique_lock<std::mutex> lck( m_responseMutex );
	
    if ( m_responses.find( id ) != m_responses.end() )
    {
        THROW_MESSAGE << "Duplicating response id #" << id;
    }

    auto it = m_requestId2SocketMap.find( id );
    if ( it == m_requestId2SocketMap.end() )
    {
        THROW_MESSAGE << "Can't find socket for the response id #" << id;
    }

	m_responses[id] = std::move( response );

    // if request is the first in chain it should be placed to general response queue 
    SOCKET sock = it->second;
	syncPutTopResponseToQueue( sock );
}

/// NOT thread safe
void ResponseDispatcher::syncPutTopResponseToQueue( SOCKET sock )
{
	auto itSock = m_requestsChains.find( sock );
    if ( itSock == m_requestsChains.end() || sock == INVALID_SOCKET )
    {
		// TODO: log
		return;
    }

    RequestIdType topId = itSock->second.front();

    if ( !m_responseWaitSentQueue.isWaiting( topId ) && !m_responseWaitSentQueue.isSent( topId ) )
    {
		m_responseWaitSentQueue.push( topId );
		m_cvResponse.notify_all();
    }
}

/// THREAD SAFE
ResponseData * ResponseDispatcher::pullResponse()
{
	std::unique_lock<std::mutex> lock( m_responseMutex );
	m_cvResponse.wait( lock, [this] () { return !m_responseWaitSentQueue.waitingEmpty() || m_bForceStop; } );

	RequestIdType id = m_responseWaitSentQueue.moveNextToSent();

	auto it = m_responses.find( id );
	if ( it != m_responses.end() )
	{
		return it->second.get();
	}
	else
	{
		return nullptr;
	}
}



/// THREAD SAFE
void ResponseDispatcher::removeResponse( RequestIdType id )
{
    std::unique_lock<std::mutex> lck( m_responseMutex );

	m_responseWaitSentQueue.remove( id );
}

SOCKET ResponseDispatcher::getSocket( RequestIdType id ) const
{
    auto it = m_requestId2SocketMap.find(id);
    if ( it == m_requestId2SocketMap.end() )
    {
		return INVALID_SOCKET;
    }

    return it->second;
}

void ResponseDispatcher::removeSocket( SOCKET sock )
{
	auto itSocket = m_requestsChains.find( sock );
    if (itSocket == m_requestsChains.end() )
    {
        THROW_MESSAGE << "Can't find socket #" << sock;
    }

	// Blocking requests and responses to avoid the case
	// when responses are cleared but new one will come 
	// before the requests are cleared also. 
	// In this case response will have no request, socket correspondency etc.

	std::unique_lock<std::mutex> lckReposnse( m_responseMutex );
	std::unique_lock<std::mutex> lckRequest( m_requestMutex );

	auto listRequestIds = itSocket->second;


	// Removing socket's responses
	for(auto id : listRequestIds)
	{ 
		m_responses.erase( m_responses.find( id ) );
		m_responseWaitSentQueue.remove( id );
	}
		
	listRequestIds.clear();


	// Removing socket's requests 
	for ( RequestIdType id : itSocket->second )
	{
		m_requestWaitSentQueue.remove( id );
		syncRemoveRequestFromChain( id );
		m_requestId2SocketMap.erase( m_requestId2SocketMap.find( id ) );
		m_requests.erase( id );
	}

	m_requestsChains.erase( itSocket );
}


void ResponseDispatcher::remove( RequestIdType id )
{
	std::unique_lock<std::mutex> lckRequest( m_requestMutex );
	std::unique_lock<std::mutex> lckResponse( m_responseMutex );

	m_responseWaitSentQueue.remove( id );
	m_responses.erase( m_responses.find( id ) );

	m_requestWaitSentQueue.remove( id );
	syncRemoveRequestFromChain( id );
	m_requestId2SocketMap.erase( m_requestId2SocketMap.find( id ) );
	m_requests.erase( m_requests.find( id ) );

	syncPutTopResponseToQueue( getSocket( id ) );
}







#include <iostream>
void ResponseDispatcher::Dump()
{
	std::stringstream ss;


	ss << std::endl << " ================================================================================================ " << std::endl;
	ss << " ResponseDispatcher::Dump() " << std::endl << std::endl;

	ss << "registered requests:";
	std::for_each(m_requests.begin(),
                    m_requests.end(),
                    [&](const auto & t) {ss << "\n\t[" << t.first << ":" << t.second->address << "] "; });
    ss << std::endl << std::endl;


    ss << "registered chains:" << std::endl;
    auto printList = [&](const std::pair<SOCKET, std::list<RequestIdType>> & item)
    {
            ss << "SOCKET " << item.first << ": ";
            std::for_each(item.second.begin(), item.second.end(), [&](const RequestIdType & i) {ss << i << " "; });
            ss << std::endl;
    };

    std::for_each(m_requestsChains.begin(), m_requestsChains.end(), printList);
    ss << std::endl;

	ss << "waiting requests:";
	std::for_each(m_requestWaitSentQueue.waitingBegin(),
                    m_requestWaitSentQueue.waitingEnd(),
                    [&](const auto & t) {ss << " " << *t; });
    ss << std::endl << std:: endl;

	ss << "sent requests:";
	std::for_each(m_requestWaitSentQueue.sentBegin(),
                    m_requestWaitSentQueue.sentEnd(),
                    [&](const auto & t) {ss << " " << *t; });
    ss << std::endl << std::endl;


    ss << "id vs sockets: ";
    std::for_each(m_requestId2SocketMap.begin(),
                    m_requestId2SocketMap.end(),
                    [&](const auto & t) {ss << "[" << t.first << ":" << t.second << "] "; });
    ss << std::endl<< std::endl;


    ss << "Stored responses: ";
    std::for_each(m_responses.begin(),
                    m_responses.end(),
                    [&](const auto & t) {ss << t.first << " "; });
    ss << std::endl;


    ss << "Top responses: " << m_responseWaitSentQueue.Dump() << std::endl << std::endl;
    ss << " ================================================================================================ " << std::endl;

	COUT_LOG << ss.str();

}