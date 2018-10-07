#include "stdafx.h"
#include "ResponseDispatcher.h"

#include <sstream>
#include <exception>

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
	m_waitingRequests.push_back( id );
	
	
    // updating response chain
    m_requestsChains[sock].push_back( id );

	m_cvRequest.notify_all();

	return id;
}

// private. METHOD IS NOT THREAD SAFE
RequestData * ResponseDispatcher::syncGetAndPumpTopRequest()
{
	if ( m_waitingRequests.empty() )
	{
		return nullptr;
	}

	RequestIdType id;

	try
	{
		id = m_waitingRequests.front();

		auto it = m_requests.find( id );
		if ( it == m_requests.end() )
		{
			THROW_MESSAGE << "Failed to find request id #" << id << " while waiting queue contains it";
		}

		RequestData * pData = it->second.get();

		m_sentRequests.push_back( id );
		m_waitingRequests.pop_front();

		return pData;
	}
	catch ( ... )
	{
		auto itWait = std::find( m_waitingRequests.begin(), m_waitingRequests.end(), id );
		if ( itWait == m_waitingRequests.end() )
		{
			m_waitingRequests.push_front( id );
		}

		auto itSent = std::find( m_sentRequests.begin(), m_sentRequests.end(), id );
		if ( itSent != m_sentRequests.end() )
		{
			m_sentRequests.erase( itSent );
		}
		throw;
	}

	return nullptr;
}


/// THREAD SAFE.
/// Moving top request from Waiting to Sent map and returninig its raw pointer.
/// If no requests are available waits until request will come
RequestData * ResponseDispatcher::getNextRequest()
{
	std::unique_lock<std::mutex> lock( m_requestMutex );
	m_cvRequest.wait( lock, [this] () {return !m_waitingRequests.empty() || m_bForceStop; } );

    if ( m_bForceStop )
    {
		DEBUG_LOG << " Throwing cTerminationException" << std::endl;
        throw cTerminationException();
    }

	return syncGetAndPumpTopRequest();
}

/// THREAD SAFE.
void ResponseDispatcher::rescheduleRequest( RequestIdType id )
{
	std::unique_lock<std::mutex> lck( m_requestMutex );

	m_waitingRequests.push_back( id );
	

	auto it = std::find( m_sentRequests.begin(), m_sentRequests.end(), id );
	if ( it != m_sentRequests.end() )
	{
		m_sentRequests.erase( it );
	}
	
}

/// public. THREAD SAFE.
void ResponseDispatcher::removeRequest( RequestIdType id )
{
	std::unique_lock<std::mutex> lck( m_requestMutex );

	syncRemoveRequestFromWaitSentMap( id );
	syncRemoveRequestFromChain( id );
	synRemoveId2SocketMapping( id );
}

/// private. NOT THREAD SAFE
void ResponseDispatcher::syncRemoveRequestFromWaitSentMap( RequestIdType id )
{
	auto itSent = std::find( m_sentRequests.begin(), m_sentRequests.end(), id );
	if ( itSent != m_sentRequests.end() )
	{
		m_sentRequests.erase( itSent );
	}

	auto itWait = std::find( m_waitingRequests.begin(), m_waitingRequests.end(), id );
	if ( itWait != m_waitingRequests.end() )
	{
		m_waitingRequests.erase( itWait );
	}
}

/// private. NOT THREAD SAFE
void ResponseDispatcher::syncRemoveRequestFromChain( RequestIdType id )
{
    auto itID = m_requestId2SocketMap.find( id );
    if ( itID == m_requestId2SocketMap.end() )
    {
        THROW_MESSAGE << "Can't find socket for the response id #" << id;
    }

    SOCKET sock = itID->second;

    auto itSock = m_requestsChains.find( sock );
    if ( itSock == m_requestsChains.end() )
    {
        THROW_MESSAGE << "Can't find chain for socket #" << sock;
    }

    itSock->second.remove( id );
}

enRequestState ResponseDispatcher::isRequestSent( RequestIdType id )
{
	auto itWait = std::find( m_waitingRequests.begin(), m_waitingRequests.end(), id );
	if ( itWait != m_waitingRequests.end() )
	{
		return enRequestState::REQ_WAITS;
	}

	auto itSent = std::find( m_sentRequests.begin(), m_sentRequests.end(), id );
	if ( itSent != m_sentRequests.end() )
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

    SOCKET sock = it->second;

	m_responses[id] = std::move( response );

    // if request is the first in chain it should be placed to general response queue 
    putTopResponseToQueue(sock);
}

SOCKET ResponseDispatcher::getSocket( RequestIdType id ) const
{
    auto it = m_requestId2SocketMap.find(id);
    if ( it == m_requestId2SocketMap.end() )
    {
        THROW_MESSAGE << "Can't find socket for the RequestId #" << id;
    }

    return it->second;
}

/// THREAD SAFE
void ResponseDispatcher::putTopResponseToQueue( SOCKET sock )
{
    auto itSock = m_requestsChains.find( sock );
    if (itSock == m_requestsChains.end() )
    {
        THROW_MESSAGE << "Can't find socket #" << sock;
    }

    RequestIdType topId = itSock->second.front();

    if ( m_responses.find( topId ) != m_responses.end() && !m_responseQueue.contains( topId ) )
    {
		m_responseQueue.push( topId );
    }
}

/// THREAD SAFE
ResponseData * ResponseDispatcher::pullResponse()
{
    RequestIdType id = m_responseQueue.pull();

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

/// IS NOT THREAD SAFE
void ResponseDispatcher::syncRemoveResponse( RequestIdType id )
{
    SOCKET sock = m_requestId2SocketMap[id];

    m_responses.erase( m_responses.find( id ) );
	m_responseQueue.remove( id );

    if ( sock != INVALID_SOCKET )
    {
		putTopResponseToQueue( sock );
    }

}

/// THREAD SAFE
void ResponseDispatcher::removeResponse( RequestIdType id )
{
    std::unique_lock<std::mutex> lck( m_responseMutex );

	syncRemoveResponse( id );
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
		m_responseQueue.remove( id );
	}
		
	listRequestIds.clear();


	// Removing socket's requests 
	for ( RequestIdType id : itSocket->second )
	{
		syncRemoveRequestFromWaitSentMap( id );
		syncRemoveRequestFromChain( id );
		synRemoveId2SocketMapping( id );
		m_requests.erase( id );
	}

	m_requestsChains.erase( itSocket );
}

/// private. NOT THREAD SAFE
void ResponseDispatcher::synRemoveId2SocketMapping( RequestIdType id )
{
	m_requestId2SocketMap.erase( m_requestId2SocketMap.find( id ) );
}



void ResponseDispatcher::removeRequestAndResponse( RequestIdType id )
{
	removeResponse( id );

	removeRequest( id );
}







#include <iostream>
void ResponseDispatcher::Dump()
{
	 std::cout << std::endl << " ================================================================================================ " << std::endl;
	 std::cout << " ResponseDispatcher::Dump() " << std::endl << std::endl;

	std::cout << "registered requests:";
	std::for_each(m_requests.begin(),
                    m_requests.end(),
                    [&](const auto & t) {std::cout << "\n\t[" << t.first << ":" << t.second->address << "] "; });
    std::cout << std::endl << std::endl;


    std::cout << "registered chains:" << std::endl;
    auto printList = [&](const std::pair<SOCKET, std::list<RequestIdType>> & item)
    {
            std::cout << "SOCKET " << item.first << ": ";
            std::for_each(item.second.begin(), item.second.end(), [&](const RequestIdType & i) {std::cout << i << " "; });
            std::cout << std::endl;
    };

    std::for_each(m_requestsChains.begin(), m_requestsChains.end(), printList);
    std::cout << std::endl;

	std::cout << "waiting requests:";
	std::for_each(m_waitingRequests.begin(),
                    m_waitingRequests.end(),
                    [&](const auto & t) {std::cout << " " << t; });
    std::cout << std::endl << std:: endl;

	std::cout << "sent requests:";
	std::for_each(m_sentRequests.begin(),
                    m_sentRequests.end(),
                    [&](const auto & t) {std::cout << " " << t; });
    std::cout << std::endl << std::endl;


    std::cout << "id vs sockets: ";
    std::for_each(m_requestId2SocketMap.begin(),
                    m_requestId2SocketMap.end(),
                    [&](const auto & t) {std::cout << "[" << t.first << ":" << t.second << "] "; });
    std::cout << std::endl<< std::endl;


    std::cout << "Stored responses: ";
    std::for_each(m_responses.begin(),
                    m_responses.end(),
                    [&](const auto & t) {std::cout << t.first << " "; });
    std::cout << std::endl;


    std::cout << "Top responses: " << m_responseQueue.Dump() << std::endl << std::endl;
    std::cout << " ================================================================================================ " << std::endl << std::endl;

}