#include "cHTTPRequestParser.h"

#include <string>
#include <iostream>
#include <string.h>
#include <time.h>
#include <vector>

#include "LogMacro.h"
#include "RequestDataTypes.h"

/**
 *  Fields format:
 *
 *  https://tools.ietf.org/html/rfc7230#page-22
 *
 *   header-field   = field-name ":" OWS field-value OWS

     field-name     = token
     field-value    = *( field-content / obs-fold )
     field-content  = field-vchar [ 1*( SP / HTAB ) field-vchar ]
     field-vchar    = VCHAR / obs-text

     obs-fold       = CRLF 1*( SP / HTAB )
                    ; obsolete line folding
                    ; see Section 3.2.4
 */

cHTTPRequestParser::cHTTPRequestParser()
{
    // TODO Auto-generated constructor stub
}

cHTTPRequestParser::~cHTTPRequestParser()
{
    // TODO Auto-generated destructor stub
}

int cHTTPRequestParser::ProcessRequest(const REQEST_DATA & reqData,
                                        REQUEST_PARAMS & reqParams) const
{
    reqParams.sock = reqData.sock;
    
    int nParamsStart = ParseFirstLine(&reqData.vecRequestBuffer[0], 
                                        reqData.vecRequestBuffer.size(), 
                                        reqParams);
    
    if ( nParamsStart <= 0)
    {
        COUT_LOG << ": " << "ParseFirstLine() failed" << std::endl;
        return 1;
    }

    int nRetVal = ParseParams(static_cast<const char *>(&reqData.vecRequestBuffer[nParamsStart]),
                                static_cast<const int &>(reqData.vecRequestBuffer.size() - nParamsStart - 1),
                                reqParams);


    if (nRetVal != 0)
    {
        COUT_LOG << ": " << "ParseParams() failed" << std::endl;
        return 2;
    }

    std::cout << "ParseParams() succeeded" << std::endl;
    std::cout << "Dump" << std::endl;
    PrintRequest(reqParams);

    return 0;
}

int cHTTPRequestParser::ParseFirstLine(const char * pchMessageBuffer,
                                        const int & NSize,
                                        REQUEST_PARAMS & requestData) const
{
    if (NSize <= 0)
    {
        COUT_LOG << "Empty request buffer" << std::endl;
        return HTTP_PARSER_RET_CODES::RET_EMPTY_REQUEST;
    }
    
    int nStart = 0;
    int nEnd = 0;

    /**
     * HTTP header first line format is
     * GET /?param=acer HTTP/1.1
     */

    // ------------- looking for HTTP method

    while ( (nEnd < NSize && pchMessageBuffer[nEnd] != ' ') &&
            (pchMessageBuffer[nEnd] != '\r') &&
            (nEnd < NSize) )
    {
        nEnd++;
    }

    if (pchMessageBuffer[nEnd] != ' ')
    {
        COUT_LOG << "Incorrect request header format" << std::endl;
        return HTTP_PARSER_RET_CODES::RET_WRONG_FORMAT;
    }

    if (nEnd - nStart == 3)
    {
        if(memcmp(pchMessageBuffer, "GET", 3) == 0)
        {
            requestData.method = HTTP_METHOD::GET;
        }
        else if (memcmp(pchMessageBuffer, "PUT", 3) == 0)
        {
            requestData.method = HTTP_METHOD::PUT;
        }
    }
    else if (nEnd - nStart == 4)
    {
        if(memcmp(pchMessageBuffer, "HEAD", 4) == 0)
        {
            requestData.method = HTTP_METHOD::HEAD;
        }
        else if (memcmp(pchMessageBuffer, "POST", 4) == 0)
        {
            requestData.method = HTTP_METHOD::POST;
        }
    }
    else if (nEnd - nStart == 5)
    {
        if(memcmp(pchMessageBuffer, "TRACE", 5) == 0)
        {
            requestData.method = HTTP_METHOD::TRACE;
        }
    }
    else if (nEnd - nStart == 6)
    {
        if(memcmp(pchMessageBuffer, "DELETE", 6) == 0)
        {
            requestData.method = HTTP_METHOD::DELETE;
        }
}
    else if (nEnd - nStart == 7)
    {
        if(memcmp(pchMessageBuffer, "CONNECT", 7) == 0)
        {
            requestData.method = HTTP_METHOD::CONNECT;
        }
        else if (memcmp(pchMessageBuffer, "OPTIONS", 7) == 0)
        {
            requestData.method = HTTP_METHOD::OPTIONS;
        }
    }
    else
    {
        COUT_LOG << "Unknown HTTP method" << std::endl;
        return HTTP_PARSER_RET_CODES::RET_UKNOWN_METHOD;
    }


    // ------------- looking for request parameters

    nStart = nEnd + 1;
    if (nStart >= NSize || pchMessageBuffer[nStart] != '/')
    {
        COUT_LOG << "No parameters section in request header" << std::endl;
        return HTTP_PARSER_RET_CODES::RET_NO_PARAMS_SECTION;
    }

    nEnd = nStart;

    while ( (nEnd < NSize && pchMessageBuffer[nEnd] != ' ') &&
            (pchMessageBuffer[nEnd] != '\r') &&
            (nEnd < NSize) )
    {
        nEnd++;
    }

    requestData.sParameters.assign(&pchMessageBuffer[nStart], nEnd - nStart);

    // ------------- looking for HTTP version

    nStart = nEnd + 1;
    nEnd = nStart;

    while ( (pchMessageBuffer[nEnd] != '\r') && (nEnd < NSize) )
    {
        nEnd++;
    }

    if ( (nStart > nEnd - 8) || (memcmp(&pchMessageBuffer[nStart], "HTTP/", 5) != 0) )
    {
        COUT_LOG << "No HTTP/ section in request header" << std::endl;
        return HTTP_PARSER_RET_CODES::RET_NO_HTTP_SECTION;
    }

    nStart += 5;

    requestData.nVersionMajor = 0;

    while (pchMessageBuffer[nStart] != '.' && nStart != nEnd)
    {
        char chDigit = GetDigit(pchMessageBuffer[nStart]);
        if (chDigit < 0)
        {
            COUT_LOG << "Incorrect HTTP version" << std::endl;
            return HTTP_PARSER_RET_CODES::RET_INCORRECT_PROTOCOL_VERSION;
        }
        requestData.nVersionMajor = requestData.nVersionMajor * 10 + chDigit;
        nStart++;
    }

    if (pchMessageBuffer[nStart] != '.')
    {
        COUT_LOG << "Incorrect HTTP version" << std::endl;
        return HTTP_PARSER_RET_CODES::RET_INCORRECT_PROTOCOL_VERSION;
    }

    nStart++;

    requestData.nVersionMinor = 0;
    while (nStart != nEnd)
    {
        char chDigit = GetDigit(pchMessageBuffer[nStart]);
        if (chDigit < 0)
        {
            COUT_LOG << "Incorrect HTTP version" << std::endl;
            return HTTP_PARSER_RET_CODES::RET_INCORRECT_PROTOCOL_VERSION;
        }
        requestData.nVersionMinor = requestData.nVersionMinor * 10 + chDigit;
        nStart++;
    }

    // --------------- parsing request header finished ----------------------------

    return nEnd + 2;
}

int cHTTPRequestParser::ParseParams(const char * pchMessageBuffer, const int & NSize, REQUEST_PARAMS & requestData) const
{
    COUT_LOG << std::endl;
    
    int nStart = 0;
    int nEnd;

    int nKeyEnd;
    int nValueStart;
    int nValueEnd;

    while (nStart < NSize)
    {
        nEnd = nStart;

        while (nEnd < NSize && pchMessageBuffer[nEnd] != ':' && pchMessageBuffer[nEnd] != '\r' )
        {
            nEnd++;
        }

        if (nEnd == NSize || pchMessageBuffer[nEnd] == '\r' )
        {
            nStart = nEnd + 2;
            continue;
        }

        nKeyEnd = (pchMessageBuffer[nEnd - 1] == ' ') ? nEnd - 2 : nEnd - 1;

        nValueStart = (pchMessageBuffer[nEnd + 1] == ' ') ? nEnd + 2 : nEnd + 1;
        nEnd = nValueStart + 1;

        while (nEnd < NSize && pchMessageBuffer[nEnd] != '\r' )
        {
            nEnd++;
        }

        nValueEnd = (pchMessageBuffer[nEnd - 1] == ' ') ? nEnd - 2 : nEnd - 1;
        if (nEnd - nValueEnd <= 0 )
        {
            nStart = nEnd + 2;
            continue;
        }

        requestData.params[std::string(&pchMessageBuffer[nStart], nKeyEnd - nStart + 1)] =
                            std::string(&pchMessageBuffer[nValueStart], nValueEnd - nValueStart + 1);

        nStart = nEnd + 2;
    }

    return 0;
}

void cHTTPRequestParser::PrintRequest(const REQUEST_PARAMS & requestData) const
{
    COUT_LOG << std::endl;

    std::cout << "HTTP method: ";

    if (requestData.method == HTTP_METHOD::ERR_METHOD)
    {
        std::cout << "HTTP_METHOD::ERR_METHOD" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::GET)
    {
        std::cout << "HTTP_METHOD::GET" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::PUT)
    {
        std::cout << "HTTP_METHOD::PUT" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::HEAD)
    {
        std::cout << "HTTP_METHOD::HEAD" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::POST)
    {
        std::cout << "HTTP_METHOD::POST" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::TRACE)
    {
        std::cout << "HTTP_METHOD::TRACE" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::DELETE)
    {
        std::cout << "HTTP_METHOD::DELETE" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::CONNECT)
    {
        std::cout << "HTTP_METHOD::CONNECT" << std::endl;
    }
    else if (requestData.method == HTTP_METHOD::OPTIONS)
    {
        std::cout << "HTTP_METHOD::OPTIONS" << std::endl;
    }

    COUT_LOG << "Request parameters: " << requestData.sParameters << std::endl;
    COUT_LOG << "HTTP version: " << requestData.nVersionMajor << "." << requestData.nVersionMinor << std::endl;


    std::cout << std::endl << "---- PARAMS -------" << std::endl;
    auto it = requestData.params.begin();

    while (it != requestData.params.end())
    {
            std::cout << it->first << " : " << it->second << std::endl;
            it++;
    }

    std::cout << std::endl;
}

inline char cHTTPRequestParser::GetDigit(char chSymbol) const
{
    switch (chSymbol)
    {
        case '0':
            return 0;
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        case '6':
            return 6;
        case '7':
            return 7;
        case '8':
            return 8;
        case '9':
            return 9;
    }

    return -1;
}
