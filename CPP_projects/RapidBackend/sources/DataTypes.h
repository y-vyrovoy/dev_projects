
#pragma once

#include <string>
#include <map>
#include <array>

enum class HTTP_METHOD {ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DELETE, CONNECT, OPTIONS};

struct RequestData
{
    // This id should be set by ConnectionManager
	// This id is used to match request and response
	unsigned int id;

	HTTP_METHOD http_method;
    std::string address;
    std::map<std::string, std::string> paramsMap;
};

struct ResponseData
{
	unsigned int id;
	std::array<char, 1024> data;
};