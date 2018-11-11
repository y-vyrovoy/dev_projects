#include "stdafx.h"
#include "FakeRequestParser.h"

#include <iostream>

#include "../Logger.h"


static unsigned int cnt = 0;

int FakeRequestParser::Parse( const std::vector<char> & request, RequestData & requestDataResult ) const
{
	DEBUG_LOG_F << request.data();

	requestDataResult.http_method = HTTP_METHOD::ERR_METHOD;
	requestDataResult.id = cnt++;
	requestDataResult.address = "http://ololo.com";
	requestDataResult.paramsMap["param1"] = "Fishes";
	requestDataResult.paramsMap["param2"] = "Birds";

	return 0;
}