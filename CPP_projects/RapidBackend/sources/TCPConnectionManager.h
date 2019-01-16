#pragma once

#include <thread>
#include <atomic>
#include <chrono>
#include <vector>
#include <WinSock2.h>


#include "DataTypes.h"
#include "Interfaces.h"
#include "SockTypes.h"
#include "RequestDispatcher.h"

using HiResTime = std::chrono::high_resolution_clock::time_point;


class TCPConnectionManager : public IConnectionManager
{

public:
	TCPConnectionManager();
	~TCPConnectionManager() {};

	TCPConnectionManager(const TCPConnectionManager &) = delete;
	TCPConnectionManager(TCPConnectionManager &&) = delete;
	TCPConnectionManager & operator= (const TCPConnectionManager &) = delete;
	TCPConnectionManager & operator= (TCPConnectionManager &&) = delete;

	void Init() override;

	void start() override;

	void stop() override;


private:

	void waitForRequestJob();
	void waitForResponseJob();

	void initWSA();
	void initListenSocket();
	void shutdown();

	int readRequest( SOCKET, std::vector<char> & );

	
	void addClientSocket( SOCKET clientSocket );
	void closeClientSocket( SOCKET clientSocket );
	void closeClientsSockets();

	
private:
	SOCKET						m_listenSocket;
	std::vector<SOCKET>			m_clientSockets;

	unsigned int				m_listenPort;
};