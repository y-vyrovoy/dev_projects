#include "stdafx.h"

#include "ServerFramework.h"
#include <exception>
#include <iostream>
#include <chrono>
#include <sstream>

#include "RequestDispatcher.h"
#include "FakeClasses/FakeConnectionManager.h"
#include "FakeClasses/FakeRequestParser.h"
#include "FakeClasses/FakeRequestHandler.h"

#include "Interfaces.h"
#include "Logger.h"
#include "MessageException.h"
#include "ConfigHelper.h"
#include "FileRequestHandler.h"

std::atomic<bool> ServerFramework::m_isServerRunning;

ServerFramework::ServerFramework()
{
	m_isInitialized = false;
}

ServerFramework::~ServerFramework()
{
}

void ServerFramework::Initialize( ConfigHelperPtr & config )
{
	std::stringstream ssError;

	try
	{
		m_config = config;
		config->dump();

		m_requestDispatcher.reset( new RequestDispatcher );

		// --------- Setting up request parser ------------
		//m_requestParser.reset( new FakeRequestParser );
		m_requestParser.reset( new RequestParser );

		// --------- Setting up request handler ------------
		//m_requestHandler.reset( new FakeRequestHandler );
		m_requestHandler.reset( new FileRequestHandler );
		m_requestHandler->Init( m_config,
								m_requestDispatcher.get(),
								[this] ( std::unique_ptr<ResponseData> response ) {onResponse( std::move( response ) ); } );


		// --------- Starting connection manager ------------
		//m_connectionManager.reset( new FakeConnectionManager );
		m_connectionManager.reset( new TCPConnectionManager );

		m_connectionManager->init( m_config );
		
		m_connectionManager->setOnRequestCallback( [this] ( SOCKET socket, const std::vector<char>& param ) {onRequest( socket, param ); } );
		m_connectionManager->setGetResponseCallback( [this] ( SOCKET & sendSocket, ResponseData * & response, std::chrono::milliseconds timeout ) {return getNextResponse( sendSocket, response, timeout ); } );
		m_connectionManager->setOnResponseSent( [this] ( RequestIdType id ) { onResponseSent( id ); } );

		m_isServerRunning = false;
		m_isInitialized = true;
	}
	catch( const std::exception & ex )
	{
		ERROR_LOG_F << "Error: [" << ex.what() << ']' << std::endl;
	}
}


int ServerFramework::StartServer()
{
	if ( !m_isInitialized )
	{
		throw std::runtime_error( "Server is not initialized" );
	}

	if ( m_isServerRunning )
	{
		throw std::runtime_error( "Server is already running. Single instance is allowed" );
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

			m_requestDispatcher->registerResponse( std::move( response ) );
		}
		else
		{
			//INFO_LOG_F << "New request."
			//			<< " [ address " << requestData->address << " ]" 
			//			<< " [ socket = " << socket << " ]";

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
	std::string logString{ response->data.begin(), response->data.end() };

	//INFO_LOG_F << "Response"
	//	<< " [id=" << response->id << "]"
	//	<< " [data=" << logString << "]";

	m_requestDispatcher->registerResponse( std::move( response ) );
}

void ServerFramework::getNextResponse( SOCKET & sendSocket, ResponseData* &  response, std::chrono::milliseconds timeoutMS )
{
	response = m_requestDispatcher->pullResponse( timeoutMS );
	
	if ( response == nullptr )
	{
		// timeout
		return;
	}

	sendSocket = m_requestDispatcher->getSocket( response->id );
}

void ServerFramework::onResponseSent( RequestIdType id )
{
	m_requestDispatcher->remove( id );
}