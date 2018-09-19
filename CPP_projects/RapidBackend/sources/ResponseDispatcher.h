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

	void registerRequest( SOCKET );
	void registerResponse( ResponsePtr );

	ResponseData * getTopResponse();

	void removeResponse(RequestIdType);

	void Dump();

	void putTopOfChainToQueue(SOCKET);

	SOCKET getSocket(RequestIdType) const;

private:
	std::map< RequestIdType, SOCKET >				m_requestId2SocketMap;
	std::map< RequestIdType, ResponsePtr >			m_responses;
	std::map< SOCKET, std::list<RequestIdType> >	m_requestsChains;
	
	BlockingQueue<RequestIdType>					m_responseQueue;

	static RequestIdType m_nextRequestID;

	std::mutex m_requestMutex;
	std::mutex m_responseMutex;
};
