#ifndef SOURCES_CHTTPREQUESTPARSER_H_
#define SOURCES_CHTTPREQUESTPARSER_H_

#include <string>
#include <map>
#include <vector>

#include "RequestDataTypes.h"


class cHTTPRequestParser {
public:
    cHTTPRequestParser();
    virtual ~cHTTPRequestParser();

    int ProcessRequest(std::vector<char> vecMessageBuffer, REQUEST_DATA & requestData) const;
    int ParseFirstLine(const char * pchMessageBuffer, const int & NSize, REQUEST_DATA & requestData) const;
    int ParseParams(const char * pchMessageBuffer, const int & NSize, REQUEST_DATA & requestData) const;

private:
    void PrintRequest(const REQUEST_DATA & requestData) const;

    char GetDigit(char chSymbol) const;
};

#endif /* SOURCES_CHTTPREQUESTPARSER_H_ */
