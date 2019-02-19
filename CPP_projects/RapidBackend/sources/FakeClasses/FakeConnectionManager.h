#pragma once

#include <thread>
#include <atomic>
#include <chrono>

#include "../DataTypes.h"
#include "../Interfaces.h"
#include "../SockTypes.h"
#include "../RequestDispatcher.h"


class FakeConnectionManager : public IConnectionManager
{

public:
	FakeConnectionManager() {};
	~FakeConnectionManager() {};

	FakeConnectionManager(const FakeConnectionManager &) = delete;
	FakeConnectionManager(FakeConnectionManager &&) = delete;
	FakeConnectionManager & operator= (const FakeConnectionManager &) = delete;
	FakeConnectionManager & operator= (FakeConnectionManager &&) = delete;

	void init( const ConfigHelperPtr & config ) override;

	void start() override;

	void stop() override;

private:

	void waitForRequestJob( StopFlagPtr forceStop );

	std::string generateRequestString();

	std::vector<char> m_fakeRequest;
};