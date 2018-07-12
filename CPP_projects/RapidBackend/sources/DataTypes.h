/* 
 * File:   Interfaces.h
 * Author: yura
 *
 * Created on July 11, 2018, 11:44 PM
 */

#ifndef DATATYPES_H
#define DATATYPES_H

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


#endif /* DATATYPES_H */

