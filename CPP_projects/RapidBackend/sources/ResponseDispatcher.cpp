#include "stdafx.h"
#include "ResponseDispatcher.h"

#include<sstream>

#include "MessageException.h"

void ResponseDispatcher::registerRequest( RequestIdType id, SOCKET sock )
{
	if (m_id2SocketMap.find(id) != m_id2SocketMap.end())
	{
		THROW_MESSAGE << "Request id duplication #" << id;
	}

	std::unique_lock<std::mutex> lck( m_Mtx );

	m_id2SocketMap[id] = sock;

	std::list<RequestIdType> & list = m_requestsChains[sock];

	auto itFirstSmaller = std::lower_bound( list.begin(), list.end(), id );
	list.insert(itFirstSmaller, id);
}

void ResponseDispatcher::registerResponse( ResponsePtr response )
{
	static char pNof[] = __FUNCTION__ ": ";

	RequestIdType id = response->id;

	std::unique_lock<std::mutex> lck( m_Mtx );

	if (m_responses.find(id) != m_responses.end())
	{
		THROW_MESSAGE << "Duplicating response id #" << id;
	}

	auto itID = m_id2SocketMap.find(id);
	if ( itID == m_id2SocketMap.end() )
	{
		THROW_MESSAGE << "Can't find socket for the response id #" << id;
	}
	
	SOCKET sock = itID->second;

	m_responses[id] = std::move(response);

	putTopOfChainToQueue(sock);
}

SOCKET ResponseDispatcher::getSocket(RequestIdType id) const
{
	auto it = m_id2SocketMap.find(id);
	if ( it == m_id2SocketMap.end() )
	{
		THROW_MESSAGE << "Can't find socket for the response id #" << id;
	}

	return it->second;
}

void ResponseDispatcher::putTopOfChainToQueue(SOCKET sock)
{
	std::unique_lock<std::mutex> lck( m_Mtx );

	auto itSock = m_requestsChains.find(sock);
	if (itSock == m_requestsChains.end() )
	{
		THROW_MESSAGE << "Can't find socket #" << sock;
	}

	RequestIdType topId = itSock->second.front();

	if ( !m_responseQueue.contains(topId) )
	{
		m_responseQueue.push(topId);
	}
}

ResponseData * ResponseDispatcher::pullResponse()
{
	RequestIdType id = m_responseQueue.pull();
	return m_responses[id].get();
}

void ResponseDispatcher::removeResponse( RequestIdType id )
{
	std::unique_lock<std::mutex> lck( m_Mtx );

	auto itID = m_id2SocketMap.find(id);
	if (itID == m_id2SocketMap.end() )
	{
		THROW_MESSAGE << "Can't find socket for the response id #" << id;
	}

	SOCKET sock = itID->second;

	auto itSock = m_requestsChains.find(sock);
	if ( itSock == m_requestsChains.end() )
	{
		THROW_MESSAGE << "Can't find socket #" << sock;
	}

	itSock->second.remove(id);

	m_responses.erase( m_responses.find(id) );

	m_id2SocketMap.erase( m_id2SocketMap.find(id) );

	putTopOfChainToQueue( sock );
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
	std::for_each(m_id2SocketMap.begin(),
					m_id2SocketMap.end(),
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