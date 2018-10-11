#pragma once

#include <map>
#include <list>
#include <set>
#include <unordered_map>
#include <condition_variable>

#include "BlockingQueue.h"
#include "SockTypes.h"
#include "DataTypes.h"
#include "WaitSentQueue.h"

class ResponseDispatcher
{
public:
	enum class enRequestState { REQ_UNKNOWN = -1, REQ_WAITS = 0, REQ_SENT };

public:
	ResponseDispatcher();
	~ResponseDispatcher() {};

	RequestIdType registerRequest( SOCKET, RequestPtr );

	void rescheduleRequest( RequestIdType id );

	enRequestState isRequestSent( RequestIdType id );

	RequestData * scheduleNextRequest();



	void registerResponse( ResponsePtr );

	ResponseData * pullResponse();

	void syncPutTopResponseToQueue( SOCKET );

	void removeResponse( RequestIdType );
	

	
	SOCKET getSocket( RequestIdType ) const;
	
	void removeSocket( SOCKET sock );



	void remove( RequestIdType id );


	size_t waitingRequestCount() { return m_requestWaitSentQueue.waitingSize(); }
	size_t sentRequestCount() { return m_requestWaitSentQueue.sentSize(); }
	size_t responsesCount() { return m_responses.size(); }
	size_t responsesQueueCount() { return m_responseWaitSentQueue.waitingSize() + m_responseWaitSentQueue.sentSize(); }



	void Dump();

private:
	RequestData * syncGetAndPumpTopRequest();

	void syncRemoveRequestFromChain( RequestIdType id );

private:



	static RequestIdType m_nextRequestID;

	std::map< RequestIdType, RequestPtr, std::less<RequestIdType> >					m_requests;
	std::map< RequestIdType, ResponsePtr, std::less<RequestIdType> >				m_responses;

	WaitSentQueue<RequestIdType>							m_requestWaitSentQueue;


	// Mapping to manage response order
	std::map< RequestIdType, SOCKET >						m_requestId2SocketMap;
	std::map< SOCKET, std::list< RequestIdType > >			m_requestsChains;

	WaitSentQueue<RequestIdType>							m_responseWaitSentQueue;

	std::mutex												m_requestMutex;
	std::mutex												m_responseMutex;

	std::condition_variable									m_cvRequest;
	std::condition_variable									m_cvResponse;

	std::atomic_bool										m_bForceStop;
};

using enRequestState = ResponseDispatcher::enRequestState;
