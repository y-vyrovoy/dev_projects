#include "stdafx.h"
#include "ResponseDispatcher.h"

#include<sstream>


ResponseDispatcher::ResponseDispatcher()
{
}


ResponseDispatcher::~ResponseDispatcher()
{
}

void ResponseDispatcher::registerRequest( RequestIdType id, SOCKET sock )
{
	static char pNof[] = __FUNCTION__ ": ";

	if (m_id2SocketMap.find(id) != m_id2SocketMap.end())
	{
		std::stringstream error;
		error << pNof << "Request id duplication #" << id;
		throw std::runtime_error( error.str() );
	}

	std::unique_lock<std::mutex> lck;

	m_id2SocketMap[id] = sock;

	std::list<RequestIdType> & list = m_topQueue[sock];

	auto itFirstSmaller = std::lower_bound( list.begin(), list.end(), id );
	list.insert(itFirstSmaller, id);
}

void ResponseDispatcher::registerResponse( ResponsePtr response )
{
	static char pNof[] = __FUNCTION__ ": ";

	RequestIdType id = response->id;

	std::unique_lock<std::mutex> lck;

	if (m_storedResponses.find(id) != m_storedResponses.end())
	{
		std::stringstream error;
		error << pNof << "Duplicating response id #" << id;
		throw std::runtime_error(error.str());
	}

	auto itID = m_id2SocketMap.find(id);
	if ( itID == m_id2SocketMap.end() )
	{
		std::stringstream error;
		error << pNof << "Can't find socket for the response id #" << id;
		throw std::runtime_error( error.str() );
	}
	
	SOCKET sock = itID->second;

	m_storedResponses[id] = std::move(response);

	putTopResponseToQueue(sock);
}

SOCKET ResponseDispatcher::getSocket(RequestIdType id) const
{
	static char pNof[] = __FUNCTION__ ": ";

	auto it = m_id2SocketMap.find(id);
	if ( it == m_id2SocketMap.end() )
	{
		std::stringstream error;
		error << pNof << "Can't find socket for the response id #" << id;
		throw std::runtime_error(error.str());
	}

	return it->second;
}

void ResponseDispatcher::putTopResponseToQueue(SOCKET sock)
{
	static char pNof[] = __FUNCTION__ ": ";

	std::unique_lock<std::mutex> lck;

	auto itSock = m_topQueue.find(sock);
	if (itSock == m_topQueue.end() )
	{
		std::stringstream error;
		error << pNof << "Can't find socket #" << sock;
		throw std::runtime_error(error.str());
	}

	RequestIdType topId = itSock->second.front();

	if ( !m_topResponces.contains(topId) )
	{
		m_topResponces.push(topId);
	}
}

ResponseData * ResponseDispatcher::pullResponse()
{
	RequestIdType id = m_topResponces.pull();
	return m_storedResponses[id].get();
}

void ResponseDispatcher::removeResponse( RequestIdType id )
{
	static char pNof[] = __FUNCTION__ ": ";

	std::unique_lock<std::mutex> lck;

	auto itID = m_id2SocketMap.find(id);
	if (itID == m_id2SocketMap.end() )
	{
		std::stringstream error;
		error << pNof << "Can't find socket for the response id #" << id;
		throw std::runtime_error(error.str());
	}

	SOCKET sock = itID->second;

	auto itSock = m_topQueue.find(sock);
	if ( itSock == m_topQueue.end() )
	{
		std::stringstream error;
		error << pNof << "Can't find socket #" << sock;
		throw std::runtime_error(error.str());
	}

	itSock->second.remove(id);

	m_storedResponses.erase( m_storedResponses.find(id) );

	m_id2SocketMap.erase( m_id2SocketMap.find(id) );

	putTopResponseToQueue( sock );
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

	std::for_each(m_topQueue.begin(), m_topQueue.end(), printList);
	std::cout << std::endl;



	std::cout << "id vs sockets: ";
	std::for_each(m_id2SocketMap.begin(),
					m_id2SocketMap.end(),
					[&](const auto & t) {std::cout << "[" << t.first << ":" << t.second << "] "; });
	std::cout << std::endl;


	std::cout << "Stored responses: ";
	std::for_each(m_storedResponses.begin(),
					m_storedResponses.end(),
					[&](const auto & t) {std::cout << t.first << " "; });
	std::cout << std::endl;


	std::cout << "Top responses: " << m_topResponces.Dump() << std::endl << std::endl;
	std::cout << " ======================== " << std::endl << std::endl;

}