#include "stdafx.h"

#include "RequestParser.h"

#include <iostream>
#include <utility>			// std::pair

#include "Logger.h"

int RequestParser::Parse( const std::vector<char> & request, RequestData & requestDataResult ) const
{
	if ( request.size() == 0 )
	{
		INFO_LOG_F << "Empty request. Size is 0";
		return 3;
	}

	int paramsStart = ParseStartLine( request, requestDataResult );

	if ( paramsStart < 0 )
	{
		SPAM_LOG_F << "ParseFirstLine() failed. Request" << std::endl
					<< "[" 
					<< request.data() << "]";
		return 1;
	}

	if ( ParseParams( request, requestDataResult ) != 0 )
	{
		SPAM_LOG_F << ": " << "ParseParams() failed";
		return 2;
	}

	return 0;
}

int RequestParser::ParseStartLine( const std::vector<char> & request, RequestData & requestData ) const
{
	size_t NSize = request.size();

	size_t nStart = 0;
	size_t nEnd = 0;

	/**
	 * HTTP header first line format is
	 * GET /?param=acer HTTP/1.1
	 */

	// HTTP method

	requestData.http_method = parseHttpMethod( request );
	if ( requestData.http_method == HTTP_METHOD::ERR_METHOD )
	{
		SPAM_LOG_F << "No HTTP method in request header";
		return RET_UKNOWN_METHOD;
	}

	// request parameters

	requestData.address = parseHeaderParams( request );
	if ( requestData.address.empty() )
	{
		SPAM_LOG_F << "No parameters section in request header";
		return RET_NO_PARAMS_SECTION;
	}


	// HTTP version

	std::pair<char, char> httpVersion = parseHttpVersion( request );
	if ( httpVersion.first == 0 && httpVersion.second == 0) 
	{
		SPAM_LOG_F << "No HTTP/ section in request header";
		return RET_NO_HTTP_SECTION;
	}


	requestData.nVersionMajor = httpVersion.first;
	requestData.nVersionMinor = httpVersion.second;


	// --------------- parsing request header finished ----------------------------

	return 0;
}


HTTP_METHOD RequestParser::charToHttpMethod( std::vector<char>::const_iterator itBegin, std::vector<char>::const_iterator itEnd )
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
	
	return HTTP_METHOD::ERR_METHOD;
}

int RequestParser::ParseParams( const std::vector<char> & request, RequestData & requestData ) const
{
	// Every line before \r\n\r\n should be like [XXX: ddddddd\r\n]
	// Parameter block finishes with the empty line [\r\n]
	// If request doesn't correspond the format retrning error value

	static char delimeter[] = { '\r', '\n', '\r', '\n' };
	size_t delimeterSize = sizeof( delimeter );

	auto itHeaderEnd = std::search( request.begin(), request.end(), delimeter, delimeter + delimeterSize );
	if ( itHeaderEnd == request.end() )
		return -1;

	auto itNewLine = std::find( request.begin(), request.end(), '\r' );
	if ( itNewLine == request.end() )
		return -1;

	itNewLine++;

	if ( *itNewLine == '\n' )
		itNewLine++;

	auto it = itNewLine;
	while ( it != request.end() )
	{
		// Param name
		auto itSemicolon = std::find( it, request.end(), ':' );
		if ( itSemicolon == request.end() )
			break;

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

		// is it the end?
		if ( ( it == request.end() ) || ( it == itHeaderEnd + 1 ) )
			break;
		
		

		if ( *it == '\n' )
			it++;
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

HTTP_METHOD RequestParser::parseHttpMethod( const std::vector<char> & vecBuffer )
{
	auto it = std::find( vecBuffer.begin(), vecBuffer.end(), ' ' );
	if ( it == vecBuffer.end() )
		return HTTP_METHOD::ERR_METHOD;

	return charToHttpMethod( vecBuffer.begin(), it );
}

std::string RequestParser::parseHeaderParams( const std::vector<char> & vecBuffer )
{
	auto itBegin = std::find( vecBuffer.begin(), vecBuffer.end(), '/' );
	if ( itBegin == vecBuffer.end() )
		return "";


	auto itEnd = std::find( itBegin, vecBuffer.end(), ' ' );
	if ( itEnd == vecBuffer.end() )
		return "";

	return std::string(itBegin, itEnd);;
}

std::pair<char, char> RequestParser::parseHttpVersion( const std::vector<char> & vecBuffer )
{
	static char token[] = { 'H', 'T', 'T', 'P', '/' };
	size_t tokenSize = sizeof( token );

	std::pair<char, char> result;

	auto itBegin = std::search( vecBuffer.begin(), vecBuffer.end(), token, token + tokenSize );
	if ( itBegin == vecBuffer.end() )
		return { 0, 0 };

	itBegin += tokenSize;

	auto itEnd = std::find( itBegin, vecBuffer.end(), '.' );
	if ( itEnd == vecBuffer.end() )
		return { 0, 0 };


	result.first = 0;
	for ( auto it = itBegin; it != itEnd; ++it )
	{
		result.first = result.first * 10 + atoi( &( *it ) );
	}


	itBegin = itEnd + 1;
	if ( itBegin == vecBuffer.end() )
		return { 0, 0 };

	itEnd = std::find( itBegin, vecBuffer.end(), '\r' );
	if ( itEnd == vecBuffer.end() )
		return { 0, 0 };


	result.second = 0;
	for ( auto it = itBegin; it != itEnd; ++it )
	{
		result.second = result.second * 10 + atoi( &( *it ) );
	}

	return result;

}

bool RequestParser::isHeaderValid( std::vector<char> & vecBuffer )
{
	static char token[] = { 'H', 'T', 'T', 'P', '/' };
	size_t tokenSize = sizeof( token );

	// getting http method
	if ( parseHttpMethod( vecBuffer ) == HTTP_METHOD::ERR_METHOD )
		return false;
	
	// getting http version
	auto it = std::search( vecBuffer.begin(), vecBuffer.end(), token, token + tokenSize );
	if ( it == vecBuffer.end() )
		return false;

	return true;
}

bool RequestParser::getHeaderLength( std::vector<char> & vecBuffer, size_t & outLength )
{
	static char delimeter[] = { '\r', '\n', '\r', '\n' };
	size_t delimeterSize = sizeof( delimeter );

	auto it = std::search( vecBuffer.begin(), vecBuffer.end(), delimeter, delimeter + delimeterSize );

	if ( it == vecBuffer.end() )
	{
		outLength = static_cast< size_t >( 0 );
		return false;
	}

	outLength = std::distance( vecBuffer.begin(), it ) + delimeterSize;
	return true;
}

bool RequestParser::getContentLength( std::vector<char> & vecBuffer, size_t & outLength )
{
	static char token[] = { 'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'L', 'e', 'n', 'g', 't', 'h', ':', ' ' };
	size_t tokenSize = sizeof( token );

	static char eol[] = { '\r', '\n' };
	size_t eolSize = sizeof( eol );



	auto itBegin = std::search( vecBuffer.begin(), vecBuffer.end(), token, token + tokenSize );

	if ( itBegin == vecBuffer.end() )
	{
		outLength = static_cast< size_t >( 0 );
		return false;
	}
	itBegin += tokenSize;



	auto itEnd = std::search( itBegin, vecBuffer.end(), eol, eol + eolSize );

	if ( itEnd == vecBuffer.end() )
	{
		outLength = static_cast< size_t >( 0 );
		return false;
	}



	std::string length( itBegin, itEnd );
	outLength = atoi( length.c_str() );

	return true;
}