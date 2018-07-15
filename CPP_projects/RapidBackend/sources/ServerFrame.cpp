#include "stdafx.h"

#include "ServerFrame.h"
#include <exception>
#include <iostream>

#include "Interfaces.h"

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
    m_requestQueue.reset(new BlockingQueue<RequestData>);
    
    m_connectionManager->setOnRequestCallback( [this](const std::string& param){onRequest(param);} );

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
        throw std::runtime_error( "Server already runs. Only one running instance is allowed" );
    }

	m_connectionManager->start();

	return 0;	
}
    
void ServerFrame::StopServer()
{   
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
	}
	catch (std::exception & ex)
	{
		std::cout << ex.what() << std::endl;
	}
}