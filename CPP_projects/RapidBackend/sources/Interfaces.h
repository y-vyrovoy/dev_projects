#pragma once

#include <string>
#include <functional>
#include <memory>
#include <thread>
#include <functional>

#include "DataTypes.h"
#include "SockTypes.h"
#include "RequestDispatcher.h"

class IRequestParser
{
public:
	virtual void Parse(const std::string & request, RequestData * ) const = 0 ;
};




class IConnectionManager
{
public:

	virtual void Init() = 0;

	virtual void setOnRequestCallback( const std::function<void( SOCKET socket, const std::string& )> & cb ) = 0;

	virtual void start() = 0;

	virtual void stop() = 0;

	virtual void registerResponse( ResponsePtr ) = 0;

protected:

	std::function<void( SOCKET socket, const std::string& )> m_onRequestCallback;

	std::thread m_workThread;

	std::atomic<bool> m_forceStopThread;
};


class IRequestHandler
{
public:
	virtual void Init( RequestDispatcher *, std::function<void( std::unique_ptr<ResponseData> )> ) = 0;

	virtual void start() = 0;
	virtual void stop() = 0;

protected:
	virtual void threadJob() = 0;

	std::thread m_workThread;
	RequestDispatcher * m_queueManager;
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;
};