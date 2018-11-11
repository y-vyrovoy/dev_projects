#pragma once

#include <string>
#include <map>
#include <vector>

#include "BlockingQueue.h"

enum class HTTP_METHOD {ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DEL, CONNECT, OPTIONS};

using RequestIdType = unsigned int;

struct RequestData
{
    // id is used to match request and response
    RequestIdType id;

    HTTP_METHOD http_method;
    std::string address;
	int nVersionMajor;
    int nVersionMinor;
    std::map<std::string, std::string> paramsMap;
	std::vector<char> data;
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