/*
 * cRequestProcessor.cpp
 *
 *  Created on: May 3, 2018
 *      Author: yura
 */

#include "cRequestProcessor.h"

#include <string>
#include <iostream>
#include <string.h>
#include <time.h>
#include <vector>


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

cRequestProcessor::cRequestProcessor()
{
    // TODO Auto-generated constructor stub
}

cRequestProcessor::~cRequestProcessor()
{
    // TODO Auto-generated destructor stub
}

void cRequestProcessor::ProcessRequest(std::vector<char> vecMessageBuffer,
                                        REQUEST_DATA & requestData) const
{
	int nParamsStart = ParseFirstLine(&vecMessageBuffer[0], vecMessageBuffer.size(), requestData);
	if ( nParamsStart <= 0)
	{
            return;
	}

	int nRetVal = ParseParams(static_cast<const char *>(&vecMessageBuffer[nParamsStart]),
                                    static_cast<const int &>(vecMessageBuffer.size() - nParamsStart - 1),
                                    requestData);


	if (nRetVal != 0)
	{
		std::cout << "ParseParams() failed" << std::endl;
		return;
	}

	std::cout << "ParseParams() succeeded" << std::endl;
	std::cout << "Dump" << std::endl;
	PrintRequest(requestData);

}

int cRequestProcessor::ParseFirstLine(const char * pchMessageBuffer, 
                                        const int & NSize, 
                                        REQUEST_DATA & requestData) const
{
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
        //std::cout << "Incorrect request header format" << std::endl;
        return -1;
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
        std::cout << "Unknown HTTP method" << std::endl;
        return -2;
    }


    // ------------- looking for request parameters

    nStart = nEnd + 1;
    if (nStart >= NSize || pchMessageBuffer[nStart] != '/')
    {
        //std::cout << "No parameters section in request header" << std::endl;
        return -3;
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
        //std::cout << "No HTTP/ section in request header" << std::endl;
        return -4;
    }

    nStart += 5;

    requestData.nVersionMajor = 0;

    while (pchMessageBuffer[nStart] != '.' && nStart != nEnd)
    {
        char chDigit = GetDigit(pchMessageBuffer[nStart]);
        if (chDigit < 0)
        {
            //std::cout << "Incorrect HTTP version" << std::endl;
            return -5;
        }
        requestData.nVersionMajor = requestData.nVersionMajor * 10 + chDigit;
        nStart++;
    }

    if (pchMessageBuffer[nStart] != '.')
    {
        //std::cout << "Incorrect HTTP version" << std::endl;
        return -6;
    }

    nStart++;

    requestData.nVersionMinor = 0;
    while (nStart != nEnd)
    {
        char chDigit = GetDigit(pchMessageBuffer[nStart]);
        if (chDigit < 0)
        {
            //std::cout << "Incorrect HTTP version" << std::endl;
            return -7;
        }
        requestData.nVersionMinor = requestData.nVersionMinor * 10 + chDigit;
        nStart++;
    }

    // --------------- parsing request header finished ----------------------------

    return nEnd + 2;
}

int cRequestProcessor::ParseParams(const char * pchMessageBuffer, const int & NSize, REQUEST_DATA & requestData) const
{
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

void cRequestProcessor::PrintRequest(const REQUEST_DATA & requestData) const
{
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

    std::cout << "Request parameters: " << requestData.sParameters << std::endl;

    std::cout << "HTTP version: " << requestData.nVersionMajor << "." << requestData.nVersionMinor << std::endl;


    std::cout << std::endl << "---- PARAMS -------" << std::endl;
    auto it = requestData.params.begin();

    while (it != requestData.params.end())
    {
            std::cout << it->first << " : " << it->second << std::endl;
            it++;
    }

    std::cout << std::endl;
}

inline char cRequestProcessor::GetDigit(char chSymbol) const
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
