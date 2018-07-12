#ifndef REQUESTDATATYPES_H
#define REQUESTDATATYPES_H

#include <map>
#include <string>
#include <vector>

enum class HTTP_METHOD {ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DELETE, CONNECT, OPTIONS};

struct REQEST_DATA
{
    int sock;
    std::vector<char> vecRequestBuffer;
};

struct REQUEST_PARAMS
{
    int sock;
    HTTP_METHOD method;
    std::string sParameters;
    int nVersionMajor;
    int nVersionMinor;
    std::map<std::string, std::string> params;
};

/*
std::string HttpMethodToStr(const HTTP_METHOD & method)
{
    switch (method)
    {
        case HTTP_METHOD::ERR_METHOD :
            return "ERR_METHOD";
        case HTTP_METHOD::GET :
            return "GET";
        case HTTP_METHOD::PUT :
            return "PUT";
        case HTTP_METHOD::HEAD :
            return "HEAD";
        case HTTP_METHOD::POST :
            return "POST";
        case HTTP_METHOD::TRACE :
            return "TRACE";
        case HTTP_METHOD::DELETE :
            return "DELETE";
        case HTTP_METHOD::CONNECT :
            return "CONNECT";
        case HTTP_METHOD::OPTIONS :
            return "OPTIONS";
        
        default:
            return "";
    }

}
*/

#endif /* REQUESTDATATYPES_H */

