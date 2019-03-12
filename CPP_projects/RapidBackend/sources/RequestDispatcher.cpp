#include "stdafx.h"
#include "RequestDispatcher.h"

#include <sstream>
#include <exception>

#include "Logger.h"
#include "MessageException.h"
#include "BaseRequestHandler.h"

RequestIdType RequestDispatcher::m_nextRequestID;

RequestDispatcher::RequestDispatcher() 
	: m_bForceStop{false}
{
}

RequestIdType RequestDispatcher::getNextRequestIdSync()
{
	return m_nextRequestID++;
}

RequestIdType RequestDispatcher::getNextRequestId()
{
	std::unique_lock<std::mutex> lck( m_requestMutex );
	return getNextRequestIdSync();
}

RequestIdType RequestDispatcher::registerRequest( SOCKET sock, RequestPtr request )
{
    std::unique_lock<std::mutex> lck( m_requestMutex );
	
	RequestIdType id = getNextRequestIdSync();
	
	request->id = id;

	INFO_LOG_F << "Registering request."
				<< " [ address " << request->address << " ]" 
				<< " [ socket = " << sock << " ]"
				<< " [ id = " << id << " ]";


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
RequestData * RequestDispatcher::getAndPumpTopRequest()
{
	if ( m_requestWaitSentQueue.isWaitingEmpty() )
	{
		return nullptr;
	}

	RequestIdType id = m_requestWaitSentQueue.moveNextToSent();

	auto it = m_requests.find( id );
	if ( it == m_requests.end() )
	{
		std::stringstream ssError;
		ssError << __FUNCTION__ ": Failed to find request for queue [ id = " << id << " ]";
		throw std::runtime_error( ssError.str() );
	}

	return it->second.get();
}


/// THREAD SAFE.
/// Moving top request from Waiting to Sent map and returninig its raw pointer.
/// If no requests are available waits until request will come
RequestData * RequestDispatcher::scheduleNextRequest()
{
	std::unique_lock<std::mutex> lock( m_requestMutex );
	m_cvRequest.wait( lock, [this] () {return !m_requestWaitSentQueue.waitingEmpty() || m_bForceStop; } );

    if ( m_bForceStop )
    {
		DEBUG_LOG_F <<  "Throwing cTerminationException";
        throw cTerminationException();
    }

	return getAndPumpTopRequest();
}


/// THREAD SAFE.
void RequestDispatcher::rescheduleRequest( RequestIdType id )
{
	std::unique_lock<std::mutex> lck( m_requestMutex );

	m_requestWaitSentQueue.moveToWaiting( id );	
}

/// private. NOT THREAD SAFE
/// removes request from id-socket map
void RequestDispatcher::syncRemoveRequestFromSocketChain( RequestIdType id )
{
    auto itID = m_requestId2SocketMap.find( id );
    if ( itID == m_requestId2SocketMap.end() )
    {
        WARN_LOG_F << "Can't find socket for the response [ id = " << id << " ]";
		return;
    }

    SOCKET sock = itID->second;

    auto itSock = m_requestsChains.find( sock );
    if ( itSock == m_requestsChains.end() )
    {
        WARN_LOG_F << "Can't find chain for socket " << sock;
		return;
    }

    itSock->second.remove( id );
}

enRequestState RequestDispatcher::isRequestSent( RequestIdType id )
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



void RequestDispatcher::registerResponse( ResponsePtr response )
{
    RequestIdType id = response->id;

	std::unique_lock<std::mutex> lck( m_responseMutex );
	
    if ( m_responses.find( id ) != m_responses.end() )
    {
		std::stringstream ssError;
		ssError <<  "Duplicating response [ id = " << id << " ]";
		throw std::runtime_error( ssError.str() );
    }

    auto it = m_requestId2SocketMap.find( id );
    if ( it == m_requestId2SocketMap.end() )
    {
		std::stringstream ssError;
		ssError << "Can't find socket for the response [ id = " << id << " ]";
		throw std::runtime_error( ssError.str() );
    }

	m_responses[id] = std::move( response );


    // if request is the first in chain it should be placed to general response queue 
    SOCKET sock = it->second;

	DEBUG_LOG_F << "soket [" << sock << "] id [" << id << "]";

	syncPutTopResponseToQueue( sock );
}

void RequestDispatcher::registerFailResponse( const SOCKET socket, const std::string & msg )
{
	ResponsePtr response( new ResponseData() );
	response->id = getNextRequestId();

	RequestIdType id = response->id;

	// mapping request with socket
    m_requestId2SocketMap[id] = socket;

    // updating response chain
    m_requestsChains[socket].push_back( id );

	response->data = BaseRequestHandler::createDefaultFailResponse( id, enErrorIdType::ERR_PARSE_METDHOD, msg );

	registerResponse( std::move( response ) );
}

void RequestDispatcher::registerFailResponse( const SOCKET socket, const RequestPtr & request )
{
	ResponsePtr response( new ResponseData() );
	response->id = getNextRequestId();

	// mapping request with socket
    m_requestId2SocketMap[response->id] = socket;

	response->data = BaseRequestHandler::createDefaultFailResponse( response->id, enErrorIdType::ERR_PARSE_METDHOD );

	registerResponse( std::move( response ) );
}


/// NOT thread safe
void RequestDispatcher::syncPutTopResponseToQueue( SOCKET sock )
{
	if ( sock == INVALID_SOCKET )
    {
		WARN_LOG_F << "sock == INVALID_SOCKET. Ignoring";
		return;
    }

	auto itSock = m_requestsChains.find( sock );
    if ( itSock == m_requestsChains.end() )
    {
		WARN_LOG_F << "Can't find queue for socket [" << sock << "]. Ignoring";
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
ResponseData * RequestDispatcher::pullResponse()
{
	std::unique_lock<std::mutex> lock( m_responseMutex );
	m_cvResponse.wait( lock, [this] () { return !m_responseWaitSentQueue.waitingEmpty() || m_bForceStop; } );

	if ( m_bForceStop )
    {
		DEBUG_LOG_F << "Throwing cTerminationException";
        throw cTerminationException();
    }

	return getResponse();
}

/// THREAD SAFE
ResponseData * RequestDispatcher::pullResponse( std::chrono::milliseconds waitMS )
{
	std::unique_lock<std::mutex> lock( m_responseMutex );
	auto ret = m_cvResponse.wait_for( lock, waitMS, [this] () { return !m_responseWaitSentQueue.waitingEmpty() || m_bForceStop; } );

	if ( !ret )
	{	
		// timeout
		return nullptr;
	}

	if ( m_bForceStop )
    {
		DEBUG_LOG_F << "Throwing cTerminationException";
        throw cTerminationException();
    }

	return getResponse();
}

ResponseData * RequestDispatcher::getResponse()
{
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
void RequestDispatcher::removeResponse( RequestIdType id )
{
    std::unique_lock<std::mutex> lck( m_responseMutex );

	m_responseWaitSentQueue.remove( id );
}

SOCKET RequestDispatcher::getSocket( RequestIdType id ) const
{
    auto it = m_requestId2SocketMap.find(id);
    if ( it == m_requestId2SocketMap.end() )
    {
		return INVALID_SOCKET;
    }

    return it->second;
}

void RequestDispatcher::removeSocket( SOCKET sock )
{
	auto itSocket = m_requestsChains.find( sock );
    if (itSocket == m_requestsChains.end() )
    {
		std::stringstream ssError;
		ssError << "Can't find socket " << sock;
		throw std::runtime_error( ssError.str() );
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
		syncRemoveRequestFromSocketChain( id );
		m_requestId2SocketMap.erase( m_requestId2SocketMap.find( id ) );
		m_requests.erase( id );
	}

	m_requestsChains.erase( itSocket );
}

void RequestDispatcher::remove( RequestIdType id )
{
	std::unique_lock<std::mutex> lckRequest( m_requestMutex );
	std::unique_lock<std::mutex> lckResponse( m_responseMutex );

	m_responseWaitSentQueue.remove( id );

	m_responses.erase( m_responses.find( id ) );

	m_requestWaitSentQueue.remove( id );
	
	syncRemoveRequestFromSocketChain( id );
	
	auto itMap = m_requestId2SocketMap.find( id );
	if( itMap != m_requestId2SocketMap.end() )
		m_requestId2SocketMap.erase( itMap );
	
	auto itRequest = m_requests.find( id );
	if( itRequest != m_requests.end() )
		m_requests.erase( itRequest );

	SOCKET s = getSocket( id );

	if ( s != INVALID_SOCKET )
		syncPutTopResponseToQueue( s );
}

void RequestDispatcher::stopWaiting()
{
	m_bForceStop = true;
	m_cvRequest.notify_all();
	m_cvResponse.notify_all();
}





#include <iostream>
void RequestDispatcher::Dump()
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

	//COUT_LOG << ss.str();

}