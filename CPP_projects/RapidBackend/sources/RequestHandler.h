#pragma once

#include <functional>

#include "DataTypes.h"

class RequestDispatcher;

class RequestHandler
{
public:
	RequestHandler();
	~RequestHandler();

	void Init( RequestDispatcher *, std::function<void( std::unique_ptr<ResponseData> )> );

	void start();
	void stop();

private:
	void threadJob();

private:
	std::thread m_workThread;
	RequestDispatcher * m_queueManager;
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;
};

