#pragma once

#include <string>
#include <map>
#include <vector>

#include "BlockingQueue.h"

enum class HTTP_METHOD {ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DEL, CONNECT, OPTIONS};

namespace
{
#define DEFINE_PROPERTY(Type, Name)	private: Type m_##Name; \
									public:	\
										const Type & get##Name() const { return m_##Name; }  \
										void set##Name( const Type & param ) { m_##Name = param; }
};

using RequestIdType = unsigned int;

class RequestData
{
public:
    // id is used to match request and response

	//RequestIdType id;

	//HTTP_METHOD http_method;
	//std::string address;
	//int nVersionMajor;
	//int nVersionMinor;
	//std::map<std::string, std::string> paramsMap;
	//std::vector<char> data;

	DEFINE_PROPERTY( RequestIdType, Id )

	DEFINE_PROPERTY( HTTP_METHOD, HTTP_method )
	DEFINE_PROPERTY( std::string, Address )
	DEFINE_PROPERTY( int, VersionMajor )
	DEFINE_PROPERTY( int, VersionMinor )
	DEFINE_PROPERTY( std::vector<char>, Data )


public: 
	std::map<std::string, std::string> & getParamsMap() { return m_ParamsMap; }

	std::string getOptional( const std::string paramName );

private: 
	std::map<std::string, std::string> m_ParamsMap;
};


class ResponseData
{

public:
	bool operator== (const ResponseData & param) const { return id == param.id; }

public:
	RequestIdType id;

	std::vector<char> data;
};

const char * getHttpMethodString( const HTTP_METHOD & method );

using RequestPtr = std::unique_ptr<RequestData>;

using ResponsePtr = std::unique_ptr<ResponseData>;