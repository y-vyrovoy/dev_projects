#pragma once

#include <thread>
#include <atomic>
#include <chrono>
#include <vector>
#include <memory>			// std::shared_ptr
#include <WinSock2.h>

#include "Utils.h"
#include "DataTypes.h"
#include "Interfaces.h"
#include "SockTypes.h"
#include "RequestDispatcher.h"

using HiResClock = std::chrono::high_resolution_clock;
using HiResTimePoint = HiResClock::time_point;
using HiResTimePointAtm = std::atomic<HiResClock::time_point>;

#define CastToSec std::chrono::duration_cast< std::chrono::seconds >
#define CastToMS std::chrono::duration_cast< std::chrono::milliseconds >
#define CastToUS std::chrono::duration_cast< std::chrono::microseconds >

using TimePoint = std::chrono::system_clock::time_point;


using namespace std::chrono_literals;

class TCPConnectionManager : public IConnectionManager
{

public:
	TCPConnectionManager();
	~TCPConnectionManager() {};

	TCPConnectionManager( const TCPConnectionManager & ) = delete;
	TCPConnectionManager( TCPConnectionManager && ) = delete;
	TCPConnectionManager & operator= ( const TCPConnectionManager & ) = delete;
	TCPConnectionManager & operator= ( TCPConnectionManager && ) = delete;

	void Init() override;

	void start() override;

	void stop() override;


private:

	void watchdogThreadFunction( StopFlagPtr forceStop );

	void waitForRequestJob( StopFlagPtr forceStop);

	void waitForResponseJob( StopFlagPtr forceStop );

	void initWSA();
	void initListenSocket();
	void shutdown();

	int readRequest( SOCKET, std::vector<char> & );

	void closeListenSocket();
	
	void addClientSocket( SOCKET clientSocket );
	void closeClientSocket( SOCKET clientSocket );
	void closeClientsSockets();


	void restartAsyn();
	void restartJob();


	void restartRequestAsync();
	void restartRequestJob();
	void startRequestThread();

	void restartResponseAsync();
	void restartResponseJob();
	void startResponseThread();

	void startWatchdogThread();

private:
	SOCKET										m_listenSocket;
	std::vector<SOCKET>							m_clientSockets;

	fd_set										m_active_fd_set;

	unsigned int								m_listenPort;

	StoppableThreadPtr							m_watchdogThread;


	HiResTimePointAtm							m_syncPoint;

	HiResTimePointAtm							m_nextRequestThreadTick;
	HiResTimePointAtm							m_nextResponseThreadTick;

	std::atomic_bool							m_requestThreadIsOn;
	std::atomic_bool							m_responseThreadIsOn;


	DEFINE_PROPERTY ( std::chrono::milliseconds, watchdogCheckPeriod)
	DEFINE_PROPERTY ( std::chrono::milliseconds, requestCheckPeriod)
	DEFINE_PROPERTY ( std::chrono::milliseconds, responseCheckPeriod)
};