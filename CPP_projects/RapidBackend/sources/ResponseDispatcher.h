#pragma once

#include <map>
#include <list>


#include "BlockingQueue.h"
#include "SockTypes.h"
#include "DataTypes.h"

class ResponseDispatcher
{

public:
	ResponseDispatcher();
	~ResponseDispatcher();

	void registerRequest( RequestIdType, SOCKET );
	void registerResponse( ResponsePtr );

	ResponseData * pullResponse();

	void removeResponse(RequestIdType);

	void Dump();

	void putTopResponseToQueue(SOCKET);

	SOCKET getSocket(RequestIdType) const;

private:
	std::map< RequestIdType, SOCKET >				m_id2SocketMap;
	std::map< RequestIdType, ResponsePtr >			m_storedResponses;
	std::map< SOCKET, std::list<RequestIdType> >	m_topQueue;
	
	BlockingQueue<RequestIdType> m_topResponces;

	std::mutex m_Mtx;
};
