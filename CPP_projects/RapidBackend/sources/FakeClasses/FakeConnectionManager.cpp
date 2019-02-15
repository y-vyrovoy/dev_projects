#include "stdafx.h"

#include <utility>
#include <algorithm>
#include <chrono>
#include <sstream>

#include "FakeConnectionManager.h"
#include "../Logger.h"
#include "../StoppableThread.h"

#define N_SOCKETS 4;

char g_fakeRequest[] =
	"GET /some/web/page?param1=12&param2=true HTTP/1.1\r\n"
	"Host: 127.0.0.1\r\n"
	"Connection: keep-alive\r\n"
	"Cache-Control: max-age=0\r\n"
	"Upgrade-Insecure-Requests: 1\r\n"
	"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36\r\n"
	"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n"
	"Accept-Encoding: gzip, deflate, br\r\n"
	"Accept-Language: en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6\r\n"
;



static unsigned int g_requestCounter = 0;

using namespace std::chrono_literals;



void FakeConnectionManager::init( const ConfigHelperPtr & config )
{
	int size = sizeof( g_fakeRequest );
	m_fakeRequest.assign( g_fakeRequest, g_fakeRequest + size );

	//m_fakeRequest.reserve( sizeof( g_fakeRequest ) );
	//std::copy( g_fakeRequest, g_fakeRequest[sizeof( g_fakeRequest )], std::back_inserter( m_fakeRequest ) );
}

void FakeConnectionManager::start()
{
	m_requestsThread.reset( new StoppableThread( [this] ( StopFlagPtr forceStop ) { waitForRequestJob( forceStop ); } ) );
	m_requestsThread->start();
}

void FakeConnectionManager::stop()
{
	m_requestsThread->stopAndJoin();
}

void FakeConnectionManager::waitForRequestJob( StopFlagPtr forceStop )
{
	try
	{
		while (!*forceStop)
		{
			std::this_thread::sleep_for( 1s );

			/// TODO: Here we get data from TCP connection, and send the string with the request to CB

			SOCKET socket = ( g_requestCounter++ ) / N_SOCKETS;
			m_onRequestCallback( socket, m_fakeRequest );

			std::this_thread::sleep_for( 500ms );
		}
	}
	catch (std::exception ex)
	{
		DEBUG_LOG_F << "Exception. error: " << ex.what();
	}
}

std::string FakeConnectionManager::generateRequestString()
{
	return { "" };
}
