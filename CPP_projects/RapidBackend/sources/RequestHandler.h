#pragma once

#include <functional>

#include "Interfaces.h"
#include "DataTypes.h"

class RequestDispatcher;

class RequestHandler : public IRequestHandler
{
public:
	RequestHandler();
	~RequestHandler();

	void Init( RequestDispatcher *, std::function<void( std::unique_ptr<ResponseData> )> ) override;

	void start() override;
	void stop() override;

	std::vector<char> createFaultResponse( RequestIdType id, enErrorIdType err ) const override;

private:
	void threadJob();

private:
	std::thread m_workThread;
	RequestDispatcher * m_queueManager;
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;
};

