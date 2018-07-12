/* 
 * File:   RequestParser.h
 * Author: yura
 *
 * Created on July 12, 2018, 9:48 AM
 */

#ifndef REQUESTPARSER_H
#define REQUESTPARSER_H

#include <string>

#include "DataTypes.h"

class RequestParser
{
public:
    RequestParser();
    ~RequestParser();

    void Parse(std::string request, RequestData * requestDataResult);
private:

};

#endif /* REQUESTPARSER_H */

