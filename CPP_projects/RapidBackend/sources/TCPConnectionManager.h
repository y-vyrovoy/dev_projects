
#pragma once

#include "BlockingQueue.h"

#include <functional>
#include <thread>
#include <atomic>

#include "DataTypes.h"
#include "Interfaces.h"

class TCPConnectionManager : public IConnectionManager
{
public:
	TCPConnectionManager();
	~TCPConnectionManager();

	void start() override;
	void stop() override;
};


// ***********************************************************************
//	
//	Fake interface implementation for testing and architecture development
//
// ***********************************************************************

class FakeConnectionManager : public IConnectionManager
{
public:
	FakeConnectionManager() {};
	~FakeConnectionManager() {};

	void start() override;
	void stop() override;

private:
	void threadJob();

private:
	std::thread m_workThread;
	std::atomic<bool> m_forceStopThread;
};