#pragma once

#include <string>
#include <functional>
#include <memory>
#include <thread>
#include <functional>
#include <vector>

#include "DataTypes.h"
#include "SockTypes.h"
#include "RequestDispatcher.h"

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
	virtual int Parse(const std::vector<char> & request, RequestData & ) const = 0 ;
};


using RequestCallbackType = std::function<void( SOCKET socket, const std::vector<char>& )>;

class IConnectionManager
{
public:

	virtual void Init() = 0;

	virtual void setOnRequestCallback( const RequestCallbackType & cb ) = 0;

	virtual void start() = 0;

	virtual void stop() = 0;

	virtual void registerResponse( ResponsePtr ) = 0;

protected:

	RequestCallbackType m_onRequestCallback;

	std::thread m_workThread;

	std::atomic<bool> m_forceStopThread;
};


class IRequestHandler
{
public:
	virtual void Init( RequestDispatcher *, std::function<void( std::unique_ptr<ResponseData> )> ) = 0;

	virtual void start() = 0;
	virtual void stop() = 0;

	virtual std::vector<char> createFaultResponse( RequestIdType id, enErrorIdType err ) const = 0 ;


protected:
	virtual void threadJob() = 0;

	std::thread m_workThread;
	RequestDispatcher * m_queueManager;
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;
};