#pragma once

#include <functional>
#include <filesystem>

#include "BaseRequestHandler.h"
#include "DataTypes.h"


class RequestDispatcher;

class FileRequestHandler : public BaseRequestHandler
{
public:
	enum class enContentType { TEXT, MULTYPART, MESSAGE, IMAGE, AUDIO, VIDEO, APPLICATION, ERR_TYPE };

	FileRequestHandler();
	~FileRequestHandler();

	void Init( const ConfigHelperPtr & config,
				RequestDispatcher * requestDispatcher, 
				std::function<void( std::unique_ptr<ResponseData> )> responseCB ) override;

	void start() override;
	void stop() override;

private:

	void threadJob();

	std::vector<char> createResponse( const RequestData * request ) const;

	const char * getContentType( const std::string & filePathname ) const;


	std::thread												m_workThread;
	
	RequestDispatcher *										m_queueManager;
	
	std::function<void( std::unique_ptr<ResponseData> )>	m_responseCallback;

	ConfigHelperPtr											m_config;

	std::string												m_rootFolder;

	std::map<std::string, std::string>						m_mapTypes;

};

