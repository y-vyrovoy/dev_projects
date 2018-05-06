/*
 * cRequestProcessor.h
 *
 *  Created on: May 3, 2018
 *      Author: yura
 */

#ifndef SOURCES_CREQUESTPROCESSOR_H_
#define SOURCES_CREQUESTPROCESSOR_H_

#include <string>

enum class HTTP_METHOD{ERR_METHOD, GET, PUT, HEAD, POST, TRACE, DELETE, CONNECT, OPTIONS};

struct REQUEST_DATA
{
	HTTP_METHOD method;
	std::string sParameters;
	int nVersionMajor;
	int nVersionMinor;
};

class cRequestProcessor {
public:
	cRequestProcessor();
	virtual ~cRequestProcessor();

	void ProcessRequest(const char * pchMessageBuffer, const int & NSize, REQUEST_DATA & requestData) const;

private:
	void PrintRequest(const REQUEST_DATA & requestData) const;

	char GetDigit(char chSymbol) const;
};

#endif /* SOURCES_CREQUESTPROCESSOR_H_ */
