#pragma once

#include <map>
#include <list>

#include "BlockingQueue.h"
#include "SockTypes.h"
#include "DataTypes.h"

class ResponseDispatcher
{

public:
	ResponseDispatcher() {};
	~ResponseDispatcher() {};

	void registerRequest( RequestIdType, SOCKET );
	void registerResponse( ResponsePtr );

	ResponseData * pullResponse();

	void removeResponse(RequestIdType);

	void Dump();

	void putTopOfChainToQueue(SOCKET);

	SOCKET getSocket(RequestIdType) const;

private:
	std::map< RequestIdType, SOCKET >				m_id2SocketMap;
	std::map< RequestIdType, ResponsePtr >			m_responses;
	std::map< SOCKET, std::list<RequestIdType> >	m_requestsChains;
	
	BlockingQueue<RequestIdType>					m_responseQueue;

	// TODO:: try to use set of mutexes
	std::mutex m_Mtx;
};
