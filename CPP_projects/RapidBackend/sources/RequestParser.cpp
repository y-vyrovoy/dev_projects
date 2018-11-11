#include "stdafx.h"

#include "RequestParser.h"

#include <iostream>

#include "Logger.h"

int RequestParser::Parse( const std::vector<char> & request, RequestData & requestDataResult ) const
{

	int paramsStart = ParseFirstLine( request, requestDataResult );

	if ( paramsStart <= 0 )
	{
		ERROR_LOG_F << "ParseFirstLine() failed. Request [" 
					<< request.data() << "]"
					<< std::endl;
		return 1;
	}

	int nRetVal = ParseParams( request, paramsStart, requestDataResult );


	//if ( nRetVal != 0 )
	//{
	//	COUT_LOG << ": " << "ParseParams() failed" << std::endl;
	//	return 2;
	//}

	std::cout << "ParseParams() succeeded" << std::endl;
	std::cout << "Dump" << std::endl;
	//PrintRequest(reqParams);

	return 0;
}

int RequestParser::ParseFirstLine( const std::vector<char> & request, RequestData & requestData ) const
{
	size_t NSize = request.size();

	size_t nStart = 0;
	size_t nEnd = 0;

	/**
	 * HTTP header first line format is
	 * GET /?param=acer HTTP/1.1
	 */

	 // ------------- looking for HTTP method

	while ( ( nEnd < NSize && request[nEnd] != ' ' ) &&
		( request[nEnd] != '\r' ) &&
		( nEnd < NSize ) )
	{
		nEnd++;
	}

	if ( request[nEnd] != ' ' )
	{
		return RET_WRONG_FORMAT;
	}

	auto itBegin = request.begin();
	auto itEnd = itBegin + ( nEnd - nStart );

	requestData.http_method = getHTTPMethod( itBegin, itEnd );
	if ( requestData.http_method == HTTP_METHOD::ERR_METHOD )
		return RET_UKNOWN_METHOD;


	// ------------- looking for request parameters

	nStart = nEnd + 1;
	if ( nStart >= NSize || request[nStart] != '/' )
	{
		COUT_LOG << "No parameters section in request header" << std::endl;
		return RET_NO_PARAMS_SECTION;
	}

	nEnd = nStart;

	while ( ( nEnd < NSize && request[nEnd] != ' ' ) &&
		( request[nEnd] != '\r' ) &&
		( nEnd < NSize ) )
	{
		nEnd++;
	}

	requestData.address.assign( &request[nStart], nEnd - nStart );

	// ------------- looking for HTTP version

	nStart = nEnd + 1;
	nEnd = nStart;

	while ( ( nEnd < NSize ) && ( request[nEnd] != '\r' ) )
	{
		nEnd++;
	}

	if ( ( nStart < nEnd - 8 ) || ( std::equal( request.begin() + nStart, request.begin() + nStart + 6, "HTTP/" ) ) )
	{
		COUT_LOG << "No HTTP/ section in request header" << std::endl;
		return RET_NO_HTTP_SECTION;
	}

	nStart += 5;

	requestData.nVersionMajor = 0;

	while ( request[nStart] != '.' && nStart != nEnd )
	{
		char chDigit = GetDigit( request[nStart] );
		if ( chDigit < 0 )
		{
			COUT_LOG << "Incorrect HTTP version" << std::endl;
			return RET_INCORRECT_PROTOCOL_VERSION;
		}
		requestData.nVersionMajor = requestData.nVersionMajor * 10 + chDigit;
		nStart++;
	}

	if ( request[nStart] != '.' )
	{
		COUT_LOG << "Incorrect HTTP version" << std::endl;
		return RET_INCORRECT_PROTOCOL_VERSION;
	}

	nStart++;

	requestData.nVersionMinor = 0;
	while ( nStart != nEnd )
	{
		char chDigit = GetDigit( request[nStart] );
		if ( chDigit < 0 )
		{
			COUT_LOG << "Incorrect HTTP version" << std::endl;
			return RET_INCORRECT_PROTOCOL_VERSION;
		}
		requestData.nVersionMinor = requestData.nVersionMinor * 10 + chDigit;
		nStart++;
	}

	// --------------- parsing request header finished ----------------------------

	return nEnd + 1;
}


HTTP_METHOD RequestParser::getHTTPMethod( std::vector<char>::const_iterator itBegin, std::vector<char>::const_iterator itEnd ) const
{
	if ( std::distance(itBegin, itEnd) == 3 )
	{
		if ( std::equal( itBegin, itEnd, "GET" ) )
		{
			return HTTP_METHOD::GET;
		}
		else if ( std::equal( itBegin, itEnd, "PUT" ) )
		{
			return HTTP_METHOD::PUT;
		}
	}
	else if ( std::distance(itBegin, itEnd) == 4 )
	{
		if ( std::equal( itBegin, itEnd, "HEAD" ) )
		{
			return HTTP_METHOD::HEAD;
		}
		else if ( std::equal( itBegin, itEnd, "POST" ) )
		{
			return HTTP_METHOD::POST;
		}
	}
	else if ( std::distance(itBegin, itEnd) == 5 )
	{
		if ( !std::equal( itBegin, itEnd, "TRACE" ) )
		{
			return HTTP_METHOD::TRACE;
		}
	}
	else if ( std::distance(itBegin, itEnd) ==  6 )
	{
		if ( std::equal( itBegin, itEnd, "DELETE" ) )
		{
			return HTTP_METHOD::DEL;
		}
	}
	else if ( std::distance(itBegin, itEnd) ==  7 )
	{
		if ( std::equal( itBegin, itEnd, "CONNECT" ) )
		{
			return HTTP_METHOD::CONNECT;
		}
		else if ( std::equal( itBegin, itEnd, "OPTIONS" ) )
		{
			return HTTP_METHOD::OPTIONS;
		}
	}
	else
	{
		return HTTP_METHOD::ERR_METHOD;
	}
}

int RequestParser::ParseParams( const std::vector<char> & request, size_t offset, RequestData & requestData ) const
{
	// Every line before /r/r should be like [XXX: ddddddd\r]
	// Parameter block finishes with the empty line [\r]
	// If request doesn't correspond the format retrning error value

	auto it = request.begin() + offset;
	while ( it != request.end() )
	{
		// Param name
		auto itSemicolon = std::find( it, request.end(), ':' );
		if ( itSemicolon == request.end() )
			return -1;

		std::string paramName( it, itSemicolon );

		//Param value
		auto itEndl = std::find( it, request.end(), '\r' );
		if ( itEndl == request.end() )
			return -1;

		if ( *( itSemicolon + 1 ) == ' ' )
			itSemicolon++;

		std::string paramValue( itSemicolon + 1, itEndl );

		requestData.paramsMap[paramName] = paramValue;

		it = itEndl + 1;
		
		if ( ( it == request.end() ) || ( *it == '\r' ) )
			break;
	}

	if ( *it == '\r' )
	{
		// TODO: get data 
	}

    return 0;
}

inline char RequestParser::GetDigit( char chSymbol ) const
{
	switch ( chSymbol )
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
