#pragma once

#include "BlockingQueue.h"

#include <functional>
#include <thread>
#include <atomic>
#include <chrono>

#include "DataTypes.h"
#include "Interfaces.h"
#include "SockTypes.h"
#include "ResponseDispatcher.h"

using HiResTime = std::chrono::high_resolution_clock::time_point;


class TCPConnectionManager
{

public:
	TCPConnectionManager();
	~TCPConnectionManager();

	TCPConnectionManager(const TCPConnectionManager &) = delete;
	TCPConnectionManager(TCPConnectionManager &&) = delete;
	TCPConnectionManager & operator= (const TCPConnectionManager &) = delete;
	TCPConnectionManager & operator= (TCPConnectionManager &&) = delete;

	void Init();

	void setOnRequestCallback(const std::function<void(const std::string&)> & cb) { m_onRequestCallback = cb; }

	void start();

	void stop();

	void registerResponse( ResponsePtr );



private:


	void waitForRequestJob();

	unsigned int registerRequest( SOCKET sock );


private:

	static RequestIdType m_nextRequestID;

	std::function<void(const std::string&)> m_onRequestCallback;

	std::unique_ptr< ResponseDispatcher > m_responseDispatcher;

	std::thread m_workThread;

	std::atomic<bool> m_forceStopThread;

	std::mutex m_getIdMtx;
};