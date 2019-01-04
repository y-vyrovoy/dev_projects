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

	// --------- Setting up request parser ------------
	//m_requestParser.reset( new FakeRequestParser );
	m_requestParser.reset( new RequestParser );

	// --------- Setting up request handler ------------
	m_requestHandler.reset( new FakeRequestHandler );
	m_requestHandler->Init( m_requestDispatcher.get(), [this] ( std::unique_ptr<ResponseData> response ) {onResponse( std::move( response ) ); } );


	// --------- Starting connection manager ------------
	//m_connectionManager.reset( new FakeConnectionManager );
	m_connectionManager.reset( new TCPConnectionManager );
	m_connectionManager->Init();
	m_connectionManager->setOnRequestCallback( [this] ( SOCKET socket, const std::vector<char>& param ) {onRequest( socket, param ); } );


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

	m_requestHandler->start();
	m_connectionManager->start();

	m_isServerRunning = true;

	return 0;
}

void ServerFramework::StopServer()
{
	m_requestHandler->stop();
	m_connectionManager->stop();
	m_isServerRunning = false;
}

void ServerFramework::onRequest( SOCKET socket, const std::vector<char> & request )
{
	try
	{
		std::unique_ptr<RequestData> requestData( new RequestData );

		if ( m_requestParser->Parse( request, *requestData ) != 0 )
		{
			ERROR_LOG_F << "Failed to parse request from " << socket << " socket";

			

			ResponsePtr response( new ResponseData() );
			response->id = m_requestDispatcher->getNextRequestId();
			response->data = m_requestHandler->createFaultResponse( response->id, enErrorIdType::ERR_PARSE_METDHOD );

			m_connectionManager->registerResponse( std::move( response ) );
		}
		else
		{
			m_requestDispatcher->registerRequest( socket, std::move( requestData ) );
		}
	}
	catch ( std::exception & ex )
	{
		ERROR_LOG_F << ex.what();
	}
}

void ServerFramework::onResponse( ResponsePtr response )
{
	INFO_LOG_F << "Response"
		<< " [id=" << response->id << "]"
		<< " [data=" << response->data.data() << "]";

	m_connectionManager->registerResponse( std::move( response ) );
}