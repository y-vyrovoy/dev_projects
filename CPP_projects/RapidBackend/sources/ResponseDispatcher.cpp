#include "stdafx.h"
#include "ResponseDispatcher.h"

#include<sstream>

#include "MessageException.h"

RequestIdType ResponseDispatcher::m_nextRequestID;

void ResponseDispatcher::registerRequest( SOCKET sock )
{
	RequestIdType id = m_nextRequestID++;

	std::unique_lock<std::mutex> lck( m_requestMutex );

	// mapping request with socket
	m_requestId2SocketMap[id] = sock;

	// updating response chain
	m_requestsChains[sock].push_back( id );
}

void ResponseDispatcher::registerResponse( ResponsePtr response )
{
	RequestIdType id = response->id;

	std::unique_lock<std::mutex> lck( m_responseMutex );

	if ( m_responses.find( id ) != m_responses.end() )
	{
		THROW_MESSAGE << "Duplicating response id #" << id;
	}

	auto itID = m_requestId2SocketMap.find( id );
	if ( itID == m_requestId2SocketMap.end() )
	{
		THROW_MESSAGE << "Can't find socket for the response id #" << id;
	}
	
	SOCKET sock = itID->second;

	m_responses[id] = std::move(response);

	// if request is the first in chain it should be placed to general response queue 
	putTopOfChainToQueue(sock);
}

SOCKET ResponseDispatcher::getSocket( RequestIdType id ) const
{
	auto it = m_requestId2SocketMap.find(id);
	if ( it == m_requestId2SocketMap.end() )
	{
		THROW_MESSAGE << "Can't find socket for the response id #" << id;
	}

	return it->second;
}

void ResponseDispatcher::putTopOfChainToQueue( SOCKET sock )
{
	//std::unique_lock<std::mutex> lck( m_responseMutex );

	auto itSock = m_requestsChains.find( sock );
	if (itSock == m_requestsChains.end() )
	{
		THROW_MESSAGE << "Can't find socket #" << sock;
	}

	RequestIdType topId = itSock->second.front();

	if ( !m_responseQueue.contains( topId ) )
	{
		m_responseQueue.push(topId);
	}
}

ResponseData * ResponseDispatcher::getTopResponse()
{
	RequestIdType id = m_responseQueue.pull();
	return m_responses[id].get();
}

void ResponseDispatcher::removeResponse( RequestIdType id )
{
	SOCKET sock = INVALID_SOCKET;

	{
		std::unique_lock<std::mutex> lck( m_requestMutex );

		auto itID = m_requestId2SocketMap.find( id );
		if ( itID == m_requestId2SocketMap.end() )
		{
			THROW_MESSAGE << "Can't find socket for the response id #" << id;
		}

		sock = itID->second;

		auto itSock = m_requestsChains.find( sock );
		if ( itSock == m_requestsChains.end() )
		{
			THROW_MESSAGE << "Can't find socket #" << sock;
		}

		itSock->second.remove( id );
	}

	{
		std::unique_lock<std::mutex> lck( m_responseMutex );
	
		m_responses.erase( m_responses.find( id ) );

		m_requestId2SocketMap.erase( m_requestId2SocketMap.find( id ) );

		if ( sock != INVALID_SOCKET )
		{
			putTopOfChainToQueue( sock );
		}
	}
}


#include <iostream>
void ResponseDispatcher::Dump()
{
	auto printList = [&](const std::pair<SOCKET, std::list<RequestIdType>> & item)
	{
		std::cout << item.first << ": ";
		std::for_each(item.second.begin(), item.second.end(), [&](const RequestIdType & i) {std::cout << i << " "; });
		std::cout << std::endl;
	};

	std::for_each(m_requestsChains.begin(), m_requestsChains.end(), printList);
	std::cout << std::endl;



	std::cout << "id vs sockets: ";
	std::for_each(m_requestId2SocketMap.begin(),
					m_requestId2SocketMap.end(),
					[&](const auto & t) {std::cout << "[" << t.first << ":" << t.second << "] "; });
	std::cout << std::endl;


	std::cout << "Stored responses: ";
	std::for_each(m_responses.begin(),
					m_responses.end(),
					[&](const auto & t) {std::cout << t.first << " "; });
	std::cout << std::endl;


	std::cout << "Top responses: " << m_responseQueue.Dump() << std::endl << std::endl;
	std::cout << " ======================== " << std::endl << std::endl;

}