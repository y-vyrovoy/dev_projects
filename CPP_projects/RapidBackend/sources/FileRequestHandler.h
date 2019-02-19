#pragma once

#include <functional>

#include "Interfaces.h"
#include "DataTypes.h"

class RequestDispatcher;

class FileRequestHandler : public IRequestHandler
{
public:
	FileRequestHandler();
	~FileRequestHandler();

	void Init( const ConfigHelperPtr & config,
				RequestDispatcher * requestDispatcher, 
				std::function<void( std::unique_ptr<ResponseData> )> responseCB ) override;

	void start() override;
	void stop() override;

	std::vector<char> createFaultResponse( RequestIdType id, enErrorIdType err ) const override;

private:

	void threadJob();


	std::thread				m_workThread;
	
	RequestDispatcher *		m_queueManager;
	
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;

	ConfigHelperPtr			m_config;

	std::string				m_rootFolder;
};

