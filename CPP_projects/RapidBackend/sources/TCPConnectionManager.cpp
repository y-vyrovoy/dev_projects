#include "stdafx.h"

#include <utility>
#include <algorithm>
#include <chrono>
#include <sstream>

#include "TCPConnectionManager.h"
#include "Logger.h"



void TCPConnectionManager::Init( )
{
	m_responseDispatcher.reset( new ResponseDispatcher );
}

void TCPConnectionManager::start()
{
	m_forceStopThread = false;
	std::thread t([this]() { waitForRequestJob(); });
	m_workThread.swap(t);
}

void TCPConnectionManager::stop()
{
	m_forceStopThread = true;
	m_workThread.join();
}

void TCPConnectionManager::waitForRequestJob()
{
	try
	{
		while (!m_forceStopThread)
		{
			std::this_thread::sleep_for(std::chrono::seconds(1));

			/// TODO: Here we get data from TCP connection, and send the string with the request to CB

			registerRequest(INVALID_SOCKET);
			m_onRequestCallback( "New test request" );
		}
	}
	catch (std::exception ex)
	{
		DEBUG_LOG << "Exception. error: " << ex.what() << std::endl;
	}
}

void TCPConnectionManager::registerRequest( SOCKET sock )
{
	static const char * pNof = __FUNCTION__;

	std::unique_lock<std::mutex> lck( m_getIdMtx );

	//m_responseDispatcher->registerRequest( sock );
}

void TCPConnectionManager::registerResponse( ResponsePtr response )
{
	static const char * pNof = __FUNCTION__;

	m_responseDispatcher->registerResponse( std::move(response) );
}
