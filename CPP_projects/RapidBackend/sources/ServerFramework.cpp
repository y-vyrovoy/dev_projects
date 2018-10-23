#include "stdafx.h"

#include "ServerFramework.h"
#include <exception>
#include <iostream>

#include "RequestDispatcher.h"
#include "FakeClasses/FakeConnectionManager.h"
#include "FakeClasses/FakeRequestParser.h"
#include "FakeClasses/FakeRequestHandler.h"

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
	m_requestDispatcher.reset( new RequestDispatcher );

	m_connectionManager.reset( new FakeConnectionManager );
	m_connectionManager->Init();
	m_connectionManager->setOnRequestCallback( [this] ( SOCKET socket, const std::string& param ) {onRequest( socket, param ); } );

	m_requestParser.reset( new FakeRequestParser );

	m_requestManager.reset( new FakeRequestHandler );
	m_requestManager->Init( m_requestDispatcher.get(), [this] ( std::unique_ptr<ResponseData> response ) {onResponse( std::move( response ) ); } );

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

void ServerFramework::onRequest( SOCKET socket, const std::string & request )
{
	try
	{
		std::unique_ptr<RequestData> requestDataPtr( new RequestData );

		m_requestParser->Parse( request, requestDataPtr.get() );

		m_requestDispatcher->registerRequest( socket, std::move( requestDataPtr ) );
	}
	catch ( std::exception & ex )
	{
		DEBUG_LOG_F << ex.what();
	}
}

void ServerFramework::onResponse( std::unique_ptr<ResponseData> response )
{
	DEBUG_LOG_F << "Response"
		<< " [id=" << response->id << "]"
		<< " [data=" << response->data.data() << "]";

	m_connectionManager->registerResponse( std::move( response ) );
}