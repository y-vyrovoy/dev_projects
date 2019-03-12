#pragma once

#include <map>
#include <list>
#include <condition_variable>
#include <mutex>
#include <chrono>

#include "BlockingQueue.h"
#include "SockTypes.h"
#include "DataTypes.h"
#include "WaitSentQueue.h"
#include "StdResponsesHelper.h"

class RequestDispatcher
{
public:
	enum class enRequestState { REQ_UNKNOWN = -1, REQ_WAITS = 0, REQ_SENT };

public:
	RequestDispatcher();
	~RequestDispatcher() {};

	void Init( StdResponseHelper * helper );

	RequestIdType registerRequest( SOCKET socket, RequestPtr request );

	void rescheduleRequest( RequestIdType id );

	enRequestState isRequestSent( RequestIdType id );

	RequestData * scheduleNextRequest();



	void registerResponse( ResponsePtr response );

	void registerFailResponse( const SOCKET socket, const std::string & msg );

	ResponseData * pullResponse();

	ResponseData * pullResponse( std::chrono::milliseconds waitMS );

	void syncPutTopResponseToQueue( SOCKET sock );

	void removeResponse( RequestIdType id );
	

	
	SOCKET getSocket( RequestIdType ) const;
	
	void removeSocket( SOCKET sock );

	void remove( RequestIdType id );


	size_t waitingRequestCount()	{ return m_requestWaitSentQueue.waitingSize(); }
	size_t sentRequestCount()		{ return m_requestWaitSentQueue.sentSize(); }
	size_t responsesCount()			{ return m_responses.size(); }
	size_t responsesQueueCount()	{ return m_responseWaitSentQueue.waitingSize() + m_responseWaitSentQueue.sentSize(); }

	void stopWaiting();

	void Dump();

	RequestIdType getNextRequestId();

private:
	RequestData * getAndPumpTopRequest();

	void syncRemoveRequestFromSocketChain( RequestIdType id );

	ResponseData * getResponse();

	RequestIdType getNextRequestIdSync();

		

	static RequestIdType									m_nextRequestID;

	StdResponseHelper	*									m_stdResponseHelper;

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

using enRequestState = RequestDispatcher::enRequestState;
