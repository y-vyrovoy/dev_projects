#pragma once

#include <functional>

#include "DataTypes.h"


class RequestHandler
{
public:
	RequestHandler();
	~RequestHandler();

	void Init( RequestQueue*, std::function<void(std::unique_ptr<ResponseData>)> );
	
	void start();
	void stop();

private:
	void threadJob();

private:
	std::thread m_workThread;
	RequestQueue * m_queueManager;
	std::function<void(std::unique_ptr<ResponseData>)> m_responseCallback;


};

