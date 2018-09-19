#include "stdafx.h"

#include "ServerFramework.h"
#include <exception>
#include <iostream>

#include "Interfaces.h"
#include "Logger.h"
#include "MessageException.h"

std::atomic<bool> ServerFramework::m_isServerRunning;

ServerFramework::ServerFramework()
{
	m_isInitialized = false;
}

ServerFramework::~ServerFramework()
{
}

void ServerFramework::Initialize()
{
	m_connectionManager.reset(new TCPConnectionManager);
    m_requestParser.reset( new FakeRequestParser );
	m_requestQueue.reset( new RequestQueue );
	m_requestManager.reset( new RequestHandler );

	m_connectionManager->Init( );

    m_connectionManager->setOnRequestCallback( [this](const std::string& param){onRequest(param);} );
	m_requestManager->Init(m_requestQueue.get(), [this](std::unique_ptr<ResponseData> response) {onResponse( std::move(response) );});

	m_isServerRunning = false;
	m_isInitialized = true;
}

int ServerFramework::StartServer()
{
	if ( !m_isInitialized )
	{
		THROW_MESSAGE << "Server is notinitialized";
	}

    if ( m_isServerRunning )
    {
        THROW_MESSAGE << "Server is already running. Single instance is allowed";
    }

	m_requestManager->start();
	m_connectionManager->start();

	return 0;	
}
    
void ServerFramework::StopServer()
{   
	m_requestManager->stop();
	m_connectionManager->stop();
    m_isServerRunning = false;
}

void ServerFramework::onRequest(const std::string & request)
{
	try
	{
		std::unique_ptr<RequestData> requestDataPtr(new RequestData);

		m_requestParser->Parse(request, requestDataPtr.get());
		
		m_requestQueue->push( std::move(requestDataPtr) );
	}
	catch (std::exception & ex)
	{
		DEBUG_LOG << ex.what() << std::endl;
	}
}

void ServerFramework::onResponse(std::unique_ptr<ResponseData> response)
{
	//std::string s();

	
	m_connectionManager->registerResponse( std::move(response) );
}