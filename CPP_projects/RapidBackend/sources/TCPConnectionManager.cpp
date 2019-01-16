#include "stdafx.h"

#include <utility>
#include <algorithm>
#include <chrono>
#include <sstream>
#include <ws2tcpip.h>

#include "TCPConnectionManager.h"
#include "Logger.h"
#include "RequestParser.h"
#include "Utils.h"


#pragma comment(lib, "ws2_32.lib")

#define DEFAULT_PORT			80
#define DEFAULT_RECV_BUF_LEN	4096

TCPConnectionManager::TCPConnectionManager()
	: m_listenSocket( INVALID_SOCKET )
	, m_listenPort( DEFAULT_PORT )
{
}

void TCPConnectionManager::Init()
{
	
}

void TCPConnectionManager::start()
{
	initWSA();
	try
	{
		initListenSocket();
	}
	catch( std::exception & ex )
	{
		WSACleanup();
		throw;
	}

	m_forceStopThread = false;
	std::thread tRequest( [this] () { waitForRequestJob(); } );
	m_requestsThread.swap( tRequest );

	std::thread tResponse( [this] () { waitForResponseJob(); } );
	m_responsesThread.swap( tResponse );
}

void TCPConnectionManager::stop()
{
	m_forceStopThread = true;
	m_requestsThread.join();
	m_responsesThread.join();

	shutdown();
}

void TCPConnectionManager::initWSA()
{
	WORD wVersionRequested;
	WSADATA wsaData;
	int iResult;

	// Initializing WSA 
	wVersionRequested = MAKEWORD( 2, 2 );
	iResult = WSAStartup( wVersionRequested, &wsaData );

	if ( iResult != 0 )
	{
		THROW_MESSAGE << "WSAStartup failed with error: " << WSAGetLastError();
	}

	if ( LOBYTE( wsaData.wVersion ) != 2 || HIBYTE( wsaData.wVersion ) != 2 )
	{
		WSACleanup();
		THROW_MESSAGE << "Could not find a usable version of Winsock.dll";
	}
}

void TCPConnectionManager::initListenSocket()
{
	int iResult;

	struct addrinfo *result = NULL;
	struct addrinfo hints;

	std::stringstream ss;
	ss << m_listenPort;

	memset( &hints, 0, sizeof( hints ) );
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = IPPROTO_TCP;
	hints.ai_flags = AI_PASSIVE;

	// Resolve the server address and port
	iResult = getaddrinfo( NULL, ss.str().c_str(), &hints, &result );
	if ( iResult != 0 )
	{
		THROW_MESSAGE << "getaddrinfo() failed with error: " << WSAGetLastError();
	}


	// Create a SOCKET for connecting to server
	m_listenSocket = socket( result->ai_family, result->ai_socktype, result->ai_protocol );
	if ( m_listenSocket == INVALID_SOCKET )
	{
		THROW_MESSAGE << "socket() failed with error: " << WSAGetLastError();
	}


	// Setup the TCP listening socket
	iResult = bind( m_listenSocket, result->ai_addr, ( int ) result->ai_addrlen );
	if ( iResult == SOCKET_ERROR )
	{
		THROW_MESSAGE << "bind() failed with error: " << WSAGetLastError();
		freeaddrinfo( result );
	}

	freeaddrinfo( result );

	// Listen for the TCP listening socket
	if ( listen( m_listenSocket, SOMAXCONN ) == SOCKET_ERROR )
	{
		THROW_MESSAGE << "listen() failed with error: " << WSAGetLastError();
	}

	INFO_LOG_F << "Listen socket is ready: " << m_listenSocket;
}

void TCPConnectionManager::shutdown()
{

	if ( m_listenSocket != INVALID_SOCKET )
	{
		INFO_LOG_F << "Closing listen socket";
		closesocket( m_listenSocket );
	}

	closeClientsSockets();

	INFO_LOG_F << "Closing WSA" << std::endl;
	WSACleanup();
}



void TCPConnectionManager::waitForRequestJob()
{
	fd_set active_fd_set;
	fd_set read_fd_set;

	timeval selectTimeout;
	selectTimeout.tv_sec = 2;
	selectTimeout.tv_usec = 0;

	// Initialize the set of active sockets.
	FD_ZERO( &active_fd_set );
	FD_SET( m_listenSocket, &active_fd_set );
		
	while ( !m_forceStopThread )
	{
		read_fd_set = active_fd_set;

		int retVal = select( FD_SETSIZE, &read_fd_set, NULL, NULL, &selectTimeout );
		if ( retVal < 0 )
		{
			ERROR_LOG_F << "select() failed with error: " << WSAGetLastError();
		}

		int readySockets = ( FD_SETSIZE < retVal ) ? FD_SETSIZE : retVal;

		// Service all the sockets with input pending.
		for ( int i = 0; i < readySockets; ++i )
		{
			SOCKET sock = read_fd_set.fd_array[i];

			if ( FD_ISSET( sock, &read_fd_set ) )
			{
				if ( sock == m_listenSocket )
				{
					// Accept a client socket
					SOCKET clientSocket = accept( m_listenSocket, NULL, NULL );
					if ( clientSocket == INVALID_SOCKET )
					{
						ERROR_LOG_F << "accept() failed with error: " << WSAGetLastError();
					}

					addClientSocket( clientSocket );

					INFO_LOG_F << "New connection. [ socket = " << clientSocket << " ]";

					FD_SET( clientSocket, &active_fd_set );
				}
				else
				{
					INFO_LOG_F << "Request recieved. [ socket = " << sock << " ]";

					// Data arriving on an already-connected socket. 
					std::vector<char> vecRequest;
					int res = readRequest( sock, vecRequest );
					
					if ( res )
					{
						INFO_LOG_F << "Closing socket [ socket = " << sock << " ]";
						FD_CLR( sock, &active_fd_set );	
						closeClientSocket( sock );
						continue;
					}

					//SPAM_LOG_F << "New request: [" << std::string( vecRequest.begin(), vecRequest.end() );

					m_onRequestCallback( sock, vecRequest );
									
					// TODO: Handle errors
				}
			}
		}
	}
}

int TCPConnectionManager::readRequest( SOCKET clientSocket, std::vector<char> & vecResult )
{
	int iResult;

	char recvBuffer[DEFAULT_RECV_BUF_LEN];
	int recvBufLen = DEFAULT_RECV_BUF_LEN;

	if( vecResult.capacity() < DEFAULT_RECV_BUF_LEN )
	{
		vecResult.reserve( DEFAULT_RECV_BUF_LEN );
	}


	size_t bytesReceived = 0;
	size_t requestLength = 0;

	bool needRecvMore = true;


	do
	{
		iResult = recv( clientSocket, recvBuffer, recvBufLen, 0 );

		if ( iResult == 0 )
		{
			return -1;
		}

		else if ( iResult > 0 )
		{	
			saveBuffer( recvBuffer, iResult, "rb_request" );
			
			bytesReceived += iResult;

			if ( bytesReceived >= vecResult.capacity() )
			{
				vecResult.reserve( vecResult.capacity() + DEFAULT_RECV_BUF_LEN );
			}

			std::copy( recvBuffer, recvBuffer + iResult, std::back_inserter( vecResult ) );
			
			// Is it HTTP request?
			if ( !RequestParser::isHeaderValid( vecResult ) )
			{
				vecResult.clear();
				return 0;
			}


			// Did we receive all the header?
			size_t headerLength;
			bool gotHeaderLength = RequestParser::getHeaderLength( vecResult, headerLength );
			
			if ( !gotHeaderLength )
				continue;

			requestLength = headerLength;

			// Does the request contain content?
			size_t contentLength;
			bool gotContentLength = RequestParser::getContentLength( vecResult, contentLength );
			
			if ( gotContentLength )
			{
				requestLength += contentLength;
			}

			// Did we received the whole request?
			if ( bytesReceived >= requestLength )
			{
				needRecvMore = false;
			}
		}

	} while ( ( iResult > 0 ) && needRecvMore );	

	return 0;
}

void TCPConnectionManager::addClientSocket( SOCKET clientSocket )
{
	m_clientSockets.push_back( clientSocket );
}

void TCPConnectionManager::closeClientSocket( SOCKET clientSocket )
{
	auto it = std::find( m_clientSockets.begin(), m_clientSockets.end(), clientSocket );
	
	if ( it == m_clientSockets.end() )
		return;

	m_clientSockets.erase( it );

	closesocket( clientSocket );
}


void TCPConnectionManager::closeClientsSockets()
{
	for ( auto it : m_clientSockets )
	{
		closesocket( it );
	}
}

void TCPConnectionManager::waitForResponseJob()
{
	if ( m_getResponseCallback == nullptr )
	{
		THROW_MESSAGE << "m_onResponseCallback is not initialized";
		return;
	}

	while ( !m_forceStopThread )
	{
		ResponseData * response;
		SOCKET sendSocket;

		m_getResponseCallback( sendSocket, response );

		size_t currentPosition = 0;
		int iResult;

		while( currentPosition < response->data.size() )
		{
			iResult = send( sendSocket, response->data.data() + currentPosition, response->data.size() - currentPosition, NULL );
			
			INFO_LOG_F << "Response sent." 
						<< " [ socket = " << sendSocket << " ]"
						<< " [ id = " << response->id << " ]";

			if ( iResult == SOCKET_ERROR )
			{
				closeClientSocket( sendSocket );
				SPAM_LOG_F << "send() failed. Error: " << WSAGetLastError();

				break;
			}

			currentPosition += iResult;
		}

		m_onResponseSentCallback( response->id );

		// very very temporary
		saveBuffer( response->data.data(), response->data.size(), "rb_responses" );
	}
}