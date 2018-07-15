#include "stdafx.h"

#include "RequestParser.h"
#include <iostream>

RequestParser::RequestParser()
{
}

RequestParser::~RequestParser()
{
}

void RequestParser::Parse( const std::string & request, RequestData * requestDataResult ) const
{
	static const char * pNof = __FUNCTION__;

	std::cout << pNof << ": " << request << std::endl;
}



// ***********************************************************************
//	
//	Fake interface implementation for testing and architecture development
//
// ***********************************************************************


void FakeRequestParser::Parse(const std::string & request, RequestData * requestDataResult) const
{
	static const char * pNof = __FUNCTION__;

	std::cout << pNof << ": " << request << std::endl;

	requestDataResult->http_method = HTTP_METHOD::ERR_METHOD;
	requestDataResult->sock = 666;
	requestDataResult->address = "http://ololo.com";
	requestDataResult->paramsMap["param1"] = "Fishes";
	requestDataResult->paramsMap["param2"] = "Birds";
	
}