#include "stdafx.h"

#include "BaseRequestHandler.h"
#include "Utils.h"


char BASE_RESPONSE_HEADER[] =
"HTTP/1.1 200 OK\r\n"
"Content-Type: text/html; charset=UTF-8\r\n"
"Connection: keep-alive\r\n"
"Webpage Content\r\n";

char BASE_RESPONSE_CONTENT_HEADER[] =
"<html>"
"<head>Default response</head>"
"<body>"
"<table border=\"1\">";

char BASE_RESPONSE_CONTENT_FOOTER[] =
"</table>"
"</body>"
"</html>";

std::vector<char> BaseRequestHandler::createFailResponse( RequestIdType id, enErrorIdType err, std::string msg ) const
{
	std::stringstream buffer;

	buffer << BASE_RESPONSE_HEADER;

	buffer << "<tr><td>id</td><td>" << id << "</td></tr>";
	
	switch ( err )
	{
	case enErrorIdType::ERR_PARSE_METDHOD:
		buffer << "<tr><td>Error</td><td>Failed to parse http method</td></tr>";
		break;
	}

	if ( !msg.empty() )
	{
		buffer << "<tr><td>Message</td><td>" << msg << "</td></tr>";
	}

	buffer << BASE_RESPONSE_CONTENT_FOOTER;

	std::string str = buffer.str();

	return Utils::sstreamToVector( buffer );
}


std::vector<char> BaseRequestHandler::createDefaultFailResponse( const RequestIdType id, const enErrorIdType err, const std::string & msg )
{
	std::stringstream buffer;

	std::stringstream bufferContent;
	bufferContent << BASE_RESPONSE_CONTENT_HEADER;

	bufferContent << "<tr><td>id</td><td>" << id << "</td></tr>";
	
	switch ( err )
	{
	case enErrorIdType::ERR_PARSE_METDHOD:
		
		buffer <<
			"HTTP/1.1 200 OK\r\n";

		bufferContent << "<tr><td>Error</td><td>Failed to parse http method</td></tr>";
		
		break;

	case enErrorIdType::ERR_CANT_FIND_FILE:
		buffer <<
			"HTTP/1.1 404 Not Found\r\n";
		break;
	}

	buffer <<
		"Content-Type: text/html; charset=UTF-8\r\n"
		"Connection: close\r\n"
		"Webpage Content\r\n";


	if ( !msg.empty() )
	{
		bufferContent << "<tr><td>Message</td><td>" << msg << "</td></tr>";
	}

	bufferContent << BASE_RESPONSE_CONTENT_FOOTER;

	size_t contentLength = bufferContent.str().size();
	
	buffer << "Content-Length: " << contentLength << "\r\n";
	
	buffer << "\r\n";
	buffer << bufferContent.str();

	return Utils::sstreamToVector( buffer );
}

std::vector<char> BaseRequestHandler::createDefaultFailResponse( const RequestIdType id, const enErrorIdType err, const RequestPtr & request )
{
	return createDefaultFailResponse( id, err, std::string( request->data.begin(), request->data.end() ) );
}


