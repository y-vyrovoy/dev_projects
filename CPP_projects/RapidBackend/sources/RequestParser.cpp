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
	DEBUG_LOG_F << ": " << request;
}