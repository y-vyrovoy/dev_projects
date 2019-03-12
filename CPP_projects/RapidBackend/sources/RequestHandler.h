#pragma once

#include <functional>

#include "Interfaces.h"
#include "DataTypes.h"


class RequestDispatcher;
class StdResponseHelper;

class RequestHandler : public IRequestHandler
{
public:
	RequestHandler();
	~RequestHandler();

	void Init( const ConfigHelperPtr & config,
				StdResponseHelper * stdResponseHelper, 
				RequestDispatcher * requestDispatcher, 
				std::function<void( std::unique_ptr<ResponseData> )> responseCB ) override;

	void start() override;
	void stop() override;

private:
	void threadJob();

private:
	std::thread m_workThread;
	RequestDispatcher * m_queueManager;
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;
};

