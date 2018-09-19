
#pragma once

#include <string>
#include <map>
#include <array>

#include "BlockingQueue.h"



enum class HTTP_METHOD {ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DEL, CONNECT, OPTIONS};

using RequestIdType = unsigned int;

struct RequestData
{
    // id should be set by ConnectionManager
    // id is used to match request and response
    RequestIdType id;

    HTTP_METHOD http_method;
    std::string address;
    std::map<std::string, std::string> paramsMap;
};

class ResponseData
{

public:
	bool operator== (const ResponseData & param) const { return id == param.id; }

public:
	RequestIdType id;
	std::array<char, 1024> data;
};


using RequestPtr = std::unique_ptr<RequestData>;
using RequestQueue = BlockingQueue<RequestPtr>;

using ResponsePtr = std::unique_ptr<ResponseData>;