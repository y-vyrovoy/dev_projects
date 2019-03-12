#include "stdafx.h"
#include "FakeRequestParser.h"

#include <iostream>

#include "../Logger.h"


static unsigned int cnt = 0;

int FakeRequestParser::Parse( const std::vector<char> & request, const RequestPtr & requestDataResult ) const
{
	DEBUG_LOG_F << request.data();

	requestDataResult->setHTTP_method( HTTP_METHOD::ERR_METHOD );
	requestDataResult->setId( cnt++ );
	requestDataResult->setAddress( "http://ololo.com" );
	requestDataResult->getParamsMap()["param1"] = "Fishes";
	requestDataResult->getParamsMap()["param2"] = "Birds";

	return 0;
}