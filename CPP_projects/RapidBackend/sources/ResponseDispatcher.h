#pragma once

#include <map>
#include <list>
#include <set>
#include <unordered_map>
#include <condition_variable>

#include "BlockingQueue.h"
#include "SockTypes.h"
#include "DataTypes.h"

class ResponseDispatcher
{
public:
	enum enRequestState { REQ_UNKNOWN = -1, REQ_WAITS = 0, REQ_SENT };

public:
	ResponseDispatcher();
	~ResponseDispatcher() {};

	RequestIdType registerRequest( SOCKET, RequestPtr );

	void rescheduleRequest( RequestIdType id );

	void removeRequest( RequestIdType id );

	enRequestState isRequestSent( RequestIdType id );

	RequestData * getNextRequest();

	RequestData * getNextRequestAndWait();



	void registerResponse( ResponsePtr );

	ResponseData * pullResponse();

	void removeResponse( RequestIdType );

	void putTopResponseToQueue( SOCKET );

	
	
	SOCKET getSocket( RequestIdType ) const;
	
	void removeSocket( SOCKET sock );

	void removeRequestAndResponse( RequestIdType id );


	void Dump();


	size_t waitingRequestCount() { return m_waitingRequests.size(); }
	size_t sentRequestCount() { return m_sentRequests.size(); }
	size_t responsesCount() { return m_responses.size(); }
	size_t responsesQueueCount() { return m_responseQueue.size(); }


private:
	RequestData * syncGetAndPumpTopRequest();

	void syncRemoveRequestFromWaitSentMap( RequestIdType id );

	void syncRemoveRequestFromChain( RequestIdType id );

	void synRemoveId2SocketMapping( RequestIdType id );

	void syncRemoveResponse( RequestIdType );

	//void removeFromResponseQueue( RequestIdType id );

private:



	static RequestIdType m_nextRequestID;

	// Requests and responses
	std::map< RequestIdType, RequestPtr, std::less<RequestIdType> >					m_requests;
	std::map< RequestIdType, ResponsePtr, std::less<RequestIdType> >				m_responses;

	// requests queues
	std::deque< RequestIdType >								m_waitingRequests;
	std::deque< RequestIdType >								m_sentRequests;


	// Mapping to manage response order
	std::map< RequestIdType, SOCKET >						m_requestId2SocketMap;
	std::map< SOCKET, std::list< RequestIdType > >			m_requestsChains;
	
	BlockingQueue<RequestIdType>							m_responseQueue;

	std::mutex												m_requestMutex;
	std::mutex												m_responseMutex;
	std::mutex												m_responseQueueMutex;

	std::condition_variable									m_cvRequest;

	std::atomic_bool										m_bForceStop;
};

using enRequestState = ResponseDispatcher::enRequestState;
