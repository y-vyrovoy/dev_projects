#include "stdafx.h"

#include "RequestParser.h"

#include <iostream>

#include "Logger.h"

RequestParser::RequestParser()
{
}

RequestParser::~RequestParser()
{
}

void RequestParser::Parse( const std::string & request, RequestData * requestDataResult ) const
{
	DEBUG_LOG << ": " << request << std::endl;
}



// ***********************************************************************
//	
//	Fake interface implementation for testing and architecture development
//
// ***********************************************************************

static unsigned int cnt = 0;

void FakeRequestParser::Parse(const std::string & request, RequestData * requestDataResult) const
{
	DEBUG_LOG << ": " << request << std::endl;

	requestDataResult->http_method = HTTP_METHOD::ERR_METHOD;
	requestDataResult->id = cnt++;
	requestDataResult->address = "http://ololo.com";
	requestDataResult->paramsMap["param1"] = "Fishes";
	requestDataResult->paramsMap["param2"] = "Birds";
}