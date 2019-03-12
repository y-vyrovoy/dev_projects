#pragma once

#include <vector>					// std::vector
#include <functional>				// std::function
#include <memory>					// std::unique_ptr

#include "DataTypes.h"
#include "ConfigHelper.h"
#include "RequestDispatcher.h"


extern char BASE_RESPONSE_HEADER[];
extern char BASE_RESPONSE_CONTENT_HEADER[];
extern char BASE_RESPONSE_CONTENT_FOOTER[];

class BaseRequestHandler
{
public:
	virtual void Init( const ConfigHelperPtr & config,
						RequestDispatcher * requestDispatcher, 
						std::function<void( std::unique_ptr<ResponseData> )> responseCB ) = 0;

	virtual void start() = 0;
	virtual void stop() = 0;

	virtual std::vector<char> createFailResponse( RequestIdType id, enErrorIdType err, std::string msg ) const;
	virtual std::vector<char> createFailResponse( RequestIdType id, enErrorIdType err ) const { return createFailResponse( id, err, "" ); };

	static std::vector<char> createDefaultFailResponse( const RequestIdType id, const enErrorIdType err, const std::string & msg );
	static std::vector<char> createDefaultFailResponse( const RequestIdType id, const enErrorIdType err ) { return createDefaultFailResponse( id, err, "" ); };

	static std::vector<char> createDefaultFailResponse( const RequestIdType id, const enErrorIdType err, const RequestPtr & request );


protected:
	virtual void threadJob() = 0;

	std::thread m_workThread;
	RequestDispatcher * m_queueManager;
	std::function<void( std::unique_ptr<ResponseData> )> m_responseCallback;
};
