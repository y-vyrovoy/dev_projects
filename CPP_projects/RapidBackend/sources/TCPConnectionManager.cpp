#include "stdafx.h"

#include <utility>
#include <algorithm>
#include <chrono>
#include <sstream>
#include <ws2tcpip.h>

#include "TCPConnectionManager.h"
#include "Logger.h"
#include "RequestParser.h"

#pragma comment(lib, "ws2_32.lib")

#define DEFAULT_PORT			80
#define DEFAULT_RECV_BUF_LEN	4096

TCPConnectionManager::TCPConnectionManager()
: m_listenSocket( INVALID_SOCKET )
, m_listenPort( DEFAULT_PORT )
{
}

void TCPConnectionManager::Init( )
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
	std::thread t( [this] () { waitForRequestJob(); } );
	m_workThread.swap( t );
}

void TCPConnectionManager::stop()
{
	m_forceStopThread = true;
	m_workThread.join();

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
}

void TCPConnectionManager::shutdown()
{

	if ( m_listenSocket != INVALID_SOCKET )
	{
		INFO_LOG_F << "Closing listen socket" << std::endl;
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
			ERROR_LOG_F << "select() failed with error: " << WSAGetLastError() << std::endl;
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
					std::cout << "Listen socket " << sock << std::endl;

					// Accept a client socket
					SOCKET clientSocket = accept( m_listenSocket, NULL, NULL );
					if ( clientSocket == INVALID_SOCKET )
					{
						ERROR_LOG_F << "accept() failed with error: " << WSAGetLastError() << std::endl;
					}

					m_clientSockets.push_back( clientSocket );

					FD_SET( clientSocket, &active_fd_set );
				}
				else
				{
					std::cout << "Established socket " << sock << std::endl;

					// Data arriving on an already-connected socket. 
					std::vector<char> vecRequest = readRequest( sock );
					m_onRequestCallback( sock, vecRequest );
										
					// TODO: Handle errors

					FD_CLR( sock, &active_fd_set );
				}
			}
		}
	}
}


std::vector<char> TCPConnectionManager::readRequest( SOCKET clientSocket )
{
	int iResult;

	char recvBuffer[DEFAULT_RECV_BUF_LEN];
	int recvBufLen = DEFAULT_RECV_BUF_LEN;

	static std::vector<char> vecResult;
	vecResult.reserve( DEFAULT_RECV_BUF_LEN );

	size_t bytesReceived = 0;
	size_t requestLength = 0;

	bool needRecvMore = true;

	do
	{
		iResult = recv( clientSocket, recvBuffer, recvBufLen, 0 );

		if ( iResult > 0 )
		{	
			bytesReceived += iResult;

			if ( bytesReceived >= vecResult.capacity() )
			{
				vecResult.reserve( vecResult.capacity() + DEFAULT_RECV_BUF_LEN );
			}

			std::copy( recvBuffer, recvBuffer + iResult, std::back_inserter( vecResult ) );
			
			// Is it HTTP request?
			if ( !RequestParser::isHeaderValid( vecResult ) )
				return std::vector<char>( 0 );


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

	return vecResult;
}

void TCPConnectionManager::closeClientsSockets()
{
	for ( auto it : m_clientSockets )
	{
		closesocket( it );
	}
}

void TCPConnectionManager::registerResponse( ResponsePtr response )
{
	
}
