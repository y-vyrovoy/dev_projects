#ifndef REQUESTDATATYPES_H
#define REQUESTDATATYPES_H

#include <map>
#include <string>

enum class HTTP_METHOD{ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DELETE, CONNECT, OPTIONS};

struct REQUEST_DATA
{
    HTTP_METHOD method;
    std::string sParameters;
    int nVersionMajor;
    int nVersionMinor;
    std::map<std::string, std::string> params;
};


#endif /* REQUESTDATATYPES_H */

