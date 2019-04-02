#pragma once

#include <thread>
#include <atomic>
#include <chrono>
#include <vector>
#include <WinSock2.h>

#include "Utils.h"
#include "DataTypes.h"
#include "Interfaces.h"
#include "SockTypes.h"
#include "RequestDispatcher.h"
#include "SocketContainer.h"

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

	void init( const ConfigHelperPtr & config ) override;

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


	void restartAsync();
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
	//std::vector<SOCKET>							m_clientSockets;
	SocketContainer								m_clientSockets;

	fd_set										m_active_fd_set;

	DEFINE_PROPERTY( std::string, listenPort )

	StoppableThreadPtr							m_watchdogThread;


	HiResTimePointAtm							m_syncPoint;

	HiResTimePointAtm							m_requestThreadLastTick;
	HiResDuration								m_requestThreadWdThreshold;
	HiResDuration								m_requestSelectTimeout;

	HiResTimePointAtm							m_responseThreadLastTick;
	HiResDuration								m_responseThreadWdThreshold;
	HiResDuration								m_responseQueueTimeout;
	
	std::atomic_bool							m_requestThreadIsOn;
	std::atomic_bool							m_responseThreadIsOn;

	HiResDuration								m_requestTimeout;


	DEFINE_PROPERTY ( std::chrono::milliseconds, watchdogCheckPeriod )
	//DEFINE_PROPERTY ( std::chrono::milliseconds, requestCheckPeriod )
	//DEFINE_PROPERTY ( std::chrono::milliseconds, responseCheckPeriod )

};