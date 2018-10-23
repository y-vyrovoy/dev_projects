#include "stdafx.h"

#include <utility>
#include <algorithm>
#include <chrono>
#include <sstream>

#include "FakeConnectionManager.h"
#include "../Logger.h"

#define N_SOCKETS 4;
static unsigned int g_requestCounter = 0;

using namespace std::chrono_literals;

void FakeConnectionManager::Init( )
{
	
}

void FakeConnectionManager::start()
{
	m_forceStopThread = false;
	std::thread t([this]() { waitForRequestJob(); });
	m_workThread.swap(t);
}

void FakeConnectionManager::stop()
{
	m_forceStopThread = true;
	m_workThread.join();
}

void FakeConnectionManager::waitForRequestJob()
{
	try
	{
		while (!m_forceStopThread)
		{
			std::this_thread::sleep_for(std::chrono::seconds(1));

			/// TODO: Here we get data from TCP connection, and send the string with the request to CB

			SOCKET socket = ( g_requestCounter++ ) / N_SOCKETS;
			m_onRequestCallback( socket, "New test request" );

			std::this_thread::sleep_for( 500ms );
		}
	}
	catch (std::exception ex)
	{
		DEBUG_LOG_F << "Exception. error: " << ex.what();
	}
}


void FakeConnectionManager::registerResponse( ResponsePtr response )
{
	
}
