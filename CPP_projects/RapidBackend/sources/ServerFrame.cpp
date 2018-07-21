#include "stdafx.h"

#include "ServerFrame.h"
#include <exception>
#include <iostream>

#include "Interfaces.h"
#include "Logger.h"

std::atomic<bool> ServerFrame::m_isServerRunning;

ServerFrame::ServerFrame()
{
	m_isInitialized = false;
}

ServerFrame::~ServerFrame()
{
}

void ServerFrame::Initialize()
{
    m_connectionManager.reset(new FakeConnectionManager);
    m_requestParser.reset(new FakeRequestParser);
    m_queueManager.reset(new RequestQueueManager);
	m_requestManager.reset(new RequestHandler);
    
    m_connectionManager->setOnRequestCallback( [this](const std::string& param){onRequest(param);} );
	m_requestManager->Init(m_queueManager, [this](std::unique_ptr<ResponseData> response) {onResponse( std::move(response) );});

	m_isServerRunning = false;
	m_isInitialized = true;
}

int ServerFrame::StartServer()
{
	if ( !m_isInitialized )
	{
		throw std::runtime_error( "Server is notinitialized" );
	}

    if ( m_isServerRunning )
    {
        throw std::runtime_error( "Server is already running. Single instance is allowed" );
    }

	m_requestManager->start();
	m_connectionManager->start();

	return 0;	
}
    
void ServerFrame::StopServer()
{   
	m_requestManager->stop();
	m_connectionManager->stop();
    m_isServerRunning = false;
}

void ServerFrame::onRequest(const std::string & request)
{
	static const char * pNof = __FUNCTION__;

	try
	{
		RequestData requestData;

		m_requestParser->Parse(request, &requestData);
		
		m_queueManager->pushRequest(requestData);
	}
	catch (std::exception & ex)
	{
		DebugLog << ex.what() << std::endl;
	}
}

void ServerFrame::onResponse(std::unique_ptr<ResponseData> response)
{
	static const char * pNof = __FUNCTION__;

	//std::string s();

	
	m_connectionManager->sendResponse(std::move(response));
}