#include "stdafx.h"

#include "StdResponsesHelper.h"

#include <sstream>

#include "Logger.h"

void StdResponseHelper::Init()
{
	m_mapNames[E200_OK] = { "OK" };

	m_mapNames[E400_BAD_REQUEST] = { "Bad Request" };
	m_mapNames[E404_CANT_FIND_FILE] = { "Not Found" };
	m_mapNames[E408_REQUEST_TIMEOUT] = { "Request Timeout" };

	m_mapNames[E500_INT_SERVER_ERROR] = { "Internal Server Error" };
	m_mapNames[E520_UNKNOWN_ERROR] = { "Unknown Error" };
}

std::vector<char> StdResponseHelper::createStdResponse( const enResponseId respId, const std::string & msg, bool closeSocket ) const
{
	try
	{
		auto itUnknown = m_mapNames.find( E520_UNKNOWN_ERROR );
		if ( itUnknown == m_mapNames.end() )
		{
			ERROR_LOG_F << "StdResponseHelper initialization has failed";
			return std::vector<char>();
		}

		auto it = m_mapNames.find( respId );

		const std::string * caption; 
		if ( it != m_mapNames.end() )
		{
			caption = &( it->second );
		}
		else
		{
			caption = &( itUnknown->second );
		}


		// compiling response content
		std::stringstream bufferContent;

		bufferContent
			<< "<!DOCTYPE html>"														"\r\n"
				"<html>"																"\r\n"
				"<head><title>Test main page</title><meta charset=\"UTF-8\"></head>"	"\r\n"
				"<body>"																"\r\n"
				"<h1>" << respId << " " << *caption << "</h1>"							"\r\n"
				"<p>" << msg << "</p>"													"\r\n"
				"</body>"																"\r\n"
				"</html>"																"\r\n"
				;

		bufferContent.seekp( 0, std::ios::end );
		size_t contentLength = static_cast< size_t >( bufferContent.tellp() );



		// compiling response header
		std::stringstream bufferHeader;

		bufferHeader
			<< "HTTP/1.1 " << respId << *caption <<										"\r\n"
				"Content-Type: text/html; charset=UTF-8"								"\r\n"
				"Connection: " << (closeSocket ? "close" : "keep-alive") <<				"\r\n"
				"Webpage Content"														"\r\n"
				"Content-Length: " << contentLength << "\r\n"
				"\r\n"
				;

		bufferHeader.seekp( 0, std::ios::end );
		size_t headerLength = static_cast< size_t >( bufferHeader.tellp() );

		std::vector<char> vecResponse;
		vecResponse.reserve( headerLength + contentLength );

		std::copy( std::istreambuf_iterator<char>( bufferHeader ), std::istreambuf_iterator<char>(), std::back_inserter( vecResponse ) );
		std::copy( std::istreambuf_iterator<char>( bufferContent ), std::istreambuf_iterator<char>(), std::back_inserter( vecResponse ) );

		return vecResponse;
	}
	catch( std::exception & ex)
	{
		ERROR_LOG_F << "Can't compile response. Error: " << ex.what();
		return std::vector<char>();
	}
}
