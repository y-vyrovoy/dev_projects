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

	void setOnRequestCallback(const RequestCallbackType & cb)  override { m_onRequestCallback = cb; }

	void start() override;

	void stop() override;

	void registerResponse( ResponsePtr ) override;


private:

	void waitForRequestJob();

	void initWSA();
	void initListenSocket();
	void shutdown();

	std::vector<char> readRequest( SOCKET clientSocket );

	void closeClientsSockets();
	

	SOCKET						m_listenSocket;
	std::vector<SOCKET>			m_clientSockets;

	unsigned int m_listenPort;

};