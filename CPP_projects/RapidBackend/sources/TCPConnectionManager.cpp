#include "stdafx.h"

#include <utility>
#include <algorithm>
#include <chrono>
#include <sstream>
#include <ws2tcpip.h>
#include <mutex>
#include <condition_variable>
#include <future>
#include <functional>

#include <ctime>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>

#include "TCPConnectionManager.h"
#include "Logger.h"
#include "RequestParser.h"
#include "Utils.h"


#pragma comment(lib, "ws2_32.lib")

#define DEFAULT_PORT			80
#define DEFAULT_RECV_BUF_LEN	4096

namespace
{
	void SpamLogHiResTP( const std::string & funcName, const HiResTimePoint & tp )
	{
		int hours = std::chrono::duration_cast< std::chrono::hours >( tp.time_since_epoch() ).count();
		int minutes = std::chrono::duration_cast< std::chrono::minutes >( tp.time_since_epoch() ).count() - hours * 60;
		long long seconds = std::chrono::duration_cast< std::chrono::seconds >( tp.time_since_epoch() ).count() - ( hours * 60 + minutes ) * 60;
		long long  ms = std::chrono::duration_cast< std::chrono::milliseconds >( tp.time_since_epoch() ).count() - ( ( hours * 60 + minutes) * 60 + seconds ) * 1000;

		SPAM_LOG << funcName << ": " << hours << ":" << minutes << ":" << seconds << "." << ms ;
	}
};

TCPConnectionManager::TCPConnectionManager()
	: m_listenSocket( INVALID_SOCKET )
	, m_listenPort( DEFAULT_PORT )
	, m_watchdogCheckPeriod( 5s )
	, m_requestCheckPeriod( 2s )
	, m_responseCheckPeriod( 2s )
	, m_requestThreadIsOn( false )
{
}

void TCPConnectionManager::Init()
{
}

void TCPConnectionManager::start()
{
	m_syncPoint = HiResClock::now();

	initWSA();
	
	startWatchdogThread();

	startRequestThread();

	startResponseThread();
}

void TCPConnectionManager::stop()
{
	m_watchdogThread->stopAndJoin();

	m_requestThreadIsOn = false;
	m_requestsThread->stopAndJoin();

	m_responseThreadIsOn = false;
	m_responsesThread->stopAndJoin();

	shutdown();
}

void TCPConnectionManager::restartAsyn()
{
	std::async( std::launch::async, [this] () { restartJob(); } );
}

void TCPConnectionManager::restartJob()
{
	try
	{
		m_requestThreadIsOn = false;
		m_responseThreadIsOn = false;

		m_requestsThread->stopAndJoin();
		m_responsesThread->stopAndJoin();

		closeListenSocket();
		closeClientsSockets();

		startRequestThread();
		startResponseThread();
	}
	catch ( std::exception & ex )
	{
		ERROR_LOG_F << "Failed to restart threads. Error: " << ex.what();
	}
}


void TCPConnectionManager::restartRequestAsync()
{
	std::async( std::launch::async, [this] () { restartRequestJob(); } );
}

void TCPConnectionManager::restartRequestJob()
{
	try
	{
		m_requestThreadIsOn = false;

		closeListenSocket();
		closeClientsSockets();
		m_requestsThread->stopAndJoin();

		startRequestThread();
	}
	catch ( std::exception & ex )
	{
		ERROR_LOG_F << "Failed to restart thread. Error: " << ex.what();
	}
}


void TCPConnectionManager::startRequestThread()
{
	INFO_LOG_F << "Starting request thread";
	
	// let's start the new one
	m_nextRequestThreadTick = m_syncPoint.load() + m_requestCheckPeriod;

	m_requestsThread.reset( new StoppableThread( [this] ( StopFlagPtr forceStop ) { waitForRequestJob( forceStop ); } ) );
	m_requestsThread->start();

	m_requestThreadIsOn = true;
}

void TCPConnectionManager::restartResponseAsync()
{
	std::async( std::launch::async, [this] () { restartResponseJob(); } );
}

void TCPConnectionManager::restartResponseJob()
{
	try
	{
		m_responseThreadIsOn = false;

		closeClientsSockets();
		m_responsesThread->stopAndJoin();
		startResponseThread();
	}
	catch ( std::exception & ex )
	{
		ERROR_LOG_F << "Failed to restart thread. Error: " << ex.what();
	}
}

void TCPConnectionManager::startResponseThread()
{
	INFO_LOG_F << "Starting response thread";
	
	// let's start the new one
	m_nextResponseThreadTick = m_syncPoint.load() + m_responseCheckPeriod;

	m_responsesThread.reset( new StoppableThread( [this] ( StopFlagPtr forceStop ) { waitForResponseJob( forceStop ); } ) );
	m_responsesThread->start();

	m_responseThreadIsOn = true;
}

void TCPConnectionManager::startWatchdogThread()
{
	INFO_LOG_F << "Starting watchdog thread";
	
	m_watchdogThread.reset( new StoppableThread( [this] (StopFlagPtr forceStop ) { watchdogThreadFunction( forceStop ); } ) );
	m_watchdogThread->start();
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

	// Initialize the set of active sockets.
	FD_ZERO( &m_active_fd_set );
	FD_SET( m_listenSocket, &m_active_fd_set );

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

void TCPConnectionManager::waitForRequestJob( StopFlagPtr forceStop )
{
	fd_set read_fd_set;

	timeval selectTimeout;

	try
	{
		initListenSocket();

		while ( !*forceStop )
		{

			///// ------------- tick issues ----------------

			// tick to let know that the thread is alive
			HiResTimePoint tpNow = HiResClock::now();

			//SpamLogHiResTP( __FUNCTION__ "\t", m_nextRequestThreadTick.load() );			
			
			// timeout should fit m_nextRequestThreadTick

			std::chrono::milliseconds timeToSleep = CastToMS( m_nextRequestThreadTick.load() - tpNow );

			selectTimeout.tv_sec = static_cast<long>( CastToSec( timeToSleep ).count() );
			selectTimeout.tv_usec = CastToUS( timeToSleep ).count() % 1000000;
					   			 


			///// ------------- essential job issues ----------------

			read_fd_set = m_active_fd_set;

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

							closeClientSocket( sock );
							continue;
						}

						m_onRequestCallback( sock, vecRequest );

						// TODO: Handle errors
					}
				}
			}

			if ( m_nextRequestThreadTick.load() <= HiResClock::now() + 50ms )
			{
				m_nextRequestThreadTick = m_nextRequestThreadTick.load() + m_requestCheckPeriod;
			}
		}

		INFO_LOG_F << "Request thread was forced to terminate [forceStop]";
	}
	catch ( std::exception & ex )
	{
		ERROR_LOG_F << "Waiting thread crashed. Closing thread. Error: " << ex.what();
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

			// Did we receive the whole request?
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
	FD_SET( clientSocket, &m_active_fd_set );

	m_clientSockets.push_back( clientSocket );
}

void TCPConnectionManager::closeListenSocket()
{
	closesocket( m_listenSocket );
}


void TCPConnectionManager::closeClientSocket( SOCKET clientSocket )
{
	FD_CLR( clientSocket, &m_active_fd_set );

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

void TCPConnectionManager::waitForResponseJob( StopFlagPtr forceStop )
{
	if ( m_getResponseCallback == nullptr )
	{
		ERROR_LOG_F << "m_onResponseCallback is not initialized. Terminating response thread";
		return;
	}

	try
	{
		while ( *forceStop == false )
		{
			///// ------------- tick issues ----------------

			// tick to let know that the thread is alive
			HiResTimePoint tpNow = HiResClock::now();

			//SpamLogHiResTP( __FUNCTION__ "\t", m_nextResponseThreadTick.load() );			
			
			// timeout should fit m_nextRequestThreadTick
			std::chrono::milliseconds timeToSleep = CastToMS( m_nextResponseThreadTick.load() - tpNow );


			///// ------------- essential job issues ----------------

			ResponseData * response;
			SOCKET sendSocket;

			// waiting while the next response will be ready
			m_getResponseCallback( sendSocket, response, timeToSleep );

			if ( response != nullptr )
			{
				size_t currentPosition = 0;
				int iResult;

				while ( currentPosition < response->data.size() )
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
			}

			if ( m_nextResponseThreadTick.load() <= HiResClock::now() + 50ms )
			{
				m_nextResponseThreadTick = m_nextResponseThreadTick.load() + m_responseCheckPeriod;
			}
		}

		INFO_LOG_F << "Response thread was forced to terminate [forceStop]";
	}
	catch ( cTerminationException & )
	{
		INFO_LOG_F << "Response thread was forced to terminate [cTerminationException]";
	}
	catch ( std::exception & ex )
	{
		ERROR_LOG_F << "Reposnse thread crashed. Error: " << ex.what();
	}
}

void TCPConnectionManager::watchdogThreadFunction( StopFlagPtr forceStop )
{
	std::mutex mut;
	std::condition_variable cv;

	HiResTimePoint tpNow = HiResClock::now();
	m_syncPoint = tpNow + m_watchdogCheckPeriod;

	while ( !*forceStop )
	{
		std::unique_lock<std::mutex> lock( mut );

		// sleeping the rest of m_watchdogCheckPeriod
		// this is necessary to be sure that watchdog period is exactly m_watchdogCheckPeriod 
		
		//SpamLogHiResTP( __FUNCTION__ " SyncPoint\t", m_syncPoint.load() );

		cv.wait_for( lock, CastToMS( m_syncPoint.load() - HiResClock::now() ) );
		if ( *forceStop )
			break;

		tpNow = HiResClock::now();


		if (  m_requestThreadIsOn && ( m_nextRequestThreadTick.load() < tpNow - 50ms) ) 
		{
			// TODO: we've lost request thread!!
			ERROR_LOG_F << "Request thread is down. Restarting request and response threads";
			restartAsyn();
		}

		if ( m_responseThreadIsOn && ( m_nextResponseThreadTick.load() < tpNow - 50ms ) )
		{
			ERROR_LOG_F << "Response thread is down. Restarting response thread";
			restartResponseAsync();
		}
		
		if ( m_syncPoint.load() <= HiResClock::now() + 50ms )
		{
			m_syncPoint.store( m_syncPoint.load() + m_watchdogCheckPeriod );
		}
	}

	INFO_LOG_F << "Watchdog thread was forced to terminate [forceStop]";
}
