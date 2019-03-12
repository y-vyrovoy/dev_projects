#pragma once

#include <string>
#include <functional>
#include <memory>
#include <thread>
#include <functional>
#include <vector>
#include <chrono>


#include "DataTypes.h"
#include "SockTypes.h"
#include "RequestDispatcher.h"
#include "StoppableThread.h"
#include "ConfigHelper.h"


class IRequestParser
{
public:
	enum HTTP_PARSER_RET_CODES 
	{
		RET_EMPTY_REQUEST = -1,
		RET_WRONG_FORMAT = -2,
		RET_UKNOWN_METHOD = -3,
		RET_NO_PARAMS_SECTION = -4,
		RET_NO_HTTP_SECTION = -5,
		RET_INCORRECT_PROTOCOL_VERSION = -6,
	};


public:
	virtual int Parse( const std::vector<char> & request, const RequestPtr & requestDataResult ) const = 0 ;
};


using RequestCallbackType = std::function<void( SOCKET, const std::vector<char>& )>;
using ResponseCallbackType = std::function<void ( SOCKET&, ResponseData* &, std::chrono::milliseconds )>;
using ReponseSentCallbackType = std::function<void(RequestIdType)>;

class IConnectionManager
{

public:

	virtual void init( const ConfigHelperPtr & config ) = 0;

	virtual void setOnRequestCallback( const RequestCallbackType & cb ) { m_onRequestCallback = cb; };

	virtual void setGetResponseCallback( const ResponseCallbackType & cb ) { m_getResponseCallback = cb; };

	virtual void setOnResponseSent( const ReponseSentCallbackType & cb ) { m_onResponseSentCallback = cb; };

	virtual void start() = 0;

	virtual void stop() = 0;

protected:

	ConfigHelperPtr			m_config;

	// Connection manager calls it when it gets new request to process
	RequestCallbackType		m_onRequestCallback;
	
	// Connection manager calls it to get next response to send
	ResponseCallbackType	m_getResponseCallback;

	// Connection manager calls it to notify response is successfully sent
	ReponseSentCallbackType m_onResponseSentCallback;

	StoppableThreadPtr		m_requestsThread;
	StoppableThreadPtr		m_responsesThread;

	std::atomic<bool>		m_forceStopThread;
};


class IRequestHandler
{
public:
	virtual void Init( const ConfigHelperPtr & config,
						StdResponseHelper * stdResponseHelper, 
						RequestDispatcher * requestDispatcher, 
						std::function<void( std::unique_ptr<ResponseData> )> responseCB ) = 0;

	virtual void start() = 0;
	virtual void stop() = 0;


protected:
	virtual void threadJob() = 0;

	std::thread m_workThread;
	RequestDispatcher * m_queueManager;
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;
};