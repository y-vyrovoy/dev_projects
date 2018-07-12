#ifndef SOURCES_CHTTPREQUESTPARSER_H_
#define SOURCES_CHTTPREQUESTPARSER_H_

#include <string>
#include <map>
#include <vector>

#include "RequestDataTypes.h"

enum HTTP_PARSER_RET_CODES 
{
    RET_EMPTY_REQUEST = -1,
    RET_WRONG_FORMAT = -2,
    RET_UKNOWN_METHOD = -3,
    RET_NO_PARAMS_SECTION = -4,
    RET_NO_HTTP_SECTION = -5,
    RET_INCORRECT_PROTOCOL_VERSION = -6,
};

class cHTTPRequestParser 
{   
public:
    cHTTPRequestParser();
    virtual ~cHTTPRequestParser();

    int ProcessRequest(const REQEST_DATA &, REQUEST_PARAMS &) const;
    int ParseFirstLine(const char * pchMessageBuffer, const int & NSize, REQUEST_PARAMS & requestData) const;
    int ParseParams(const char * pchMessageBuffer, const int & NSize, REQUEST_PARAMS & requestData) const;

private:
    void PrintRequest(const REQUEST_PARAMS & requestData) const;
    char GetDigit(char chSymbol) const;
};

#endif /* SOURCES_CHTTPREQUESTPARSER_H_ */
