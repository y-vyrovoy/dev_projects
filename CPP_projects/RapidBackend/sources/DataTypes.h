
#pragma once

#include <string>
#include <map>

enum class HTTP_METHOD {ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DELETE, CONNECT, OPTIONS};

struct RequestData
{
    int sock;
    HTTP_METHOD http_method;
    std::string address;
    std::map<int, std::string> paramsMap;    
};
