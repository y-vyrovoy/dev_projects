#include "stdafx.h"

#include <utility>
#include <algorithm>
#include <chrono>

#include <sstream>

#include "TCPConnectionManager.h"

TCPConnectionManager::TCPConnectionManager()
{
}

TCPConnectionManager::~TCPConnectionManager()
{
}

void TCPConnectionManager::start()
{

}

void TCPConnectionManager::stop()
{

}



// ***********************************************************************
//	
//	Fake interface implementation for testing and architecture development
//
// ***********************************************************************

void FakeConnectionManager::start()
{
	m_forceStopThread = false;
	std::thread t( [this]() { threadJob(); } );
	m_workThread.swap( t );
}

void FakeConnectionManager::stop()
{
	m_forceStopThread = true;
	m_workThread.join();
}

void FakeConnectionManager::threadJob()
{
	static const char * pNof = __FUNCTION__;
	static int cnt = 0;

	while ( !m_forceStopThread )
	{
		std::this_thread::sleep_for( std::chrono::seconds( 1 ) );
		
		std::stringstream message;
		message << "The message #" << cnt++;

		m_onRequestCallback( message.str() );
	}
}

