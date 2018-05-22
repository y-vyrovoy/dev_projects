#ifndef SOURCES_CREQUESTPROCESSOR_H_
#define SOURCES_CREQUESTPROCESSOR_H_

#include <string>
#include <map>
#include <vector>

enum class HTTP_METHOD{ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DELETE, CONNECT, OPTIONS};

struct REQUEST_DATA
{
    HTTP_METHOD method;
    std::string sParameters;
    int nVersionMajor;
    int nVersionMinor;
    std::map<std::string, std::string> params;
};

class cRequestProcessor {
public:
    cRequestProcessor();
    virtual ~cRequestProcessor();

    void ProcessRequest(std::vector<char> vecMessageBuffer, REQUEST_DATA & requestData) const;
    int ParseFirstLine(const char * pchMessageBuffer, const int & NSize, REQUEST_DATA & requestData) const;
    int ParseParams(const char * pchMessageBuffer, const int & NSize, REQUEST_DATA & requestData) const;

private:
    void PrintRequest(const REQUEST_DATA & requestData) const;

    char GetDigit(char chSymbol) const;
};

#endif /* SOURCES_CREQUESTPROCESSOR_H_ */
