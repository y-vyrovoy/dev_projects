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

	void Init() override;

	void setOnRequestCallback(const std::function<void( SOCKET socket, const std::string& )> & cb)  override { m_onRequestCallback = cb; }

	void start() override;

	void stop() override;

	void registerResponse( ResponsePtr ) override;



private:

	void waitForRequestJob();

};