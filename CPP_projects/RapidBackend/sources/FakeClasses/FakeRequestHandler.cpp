#include "stdafx.h"
#define NOMINMAX

#include "FakeRequestHandler.h"

#include <cstring>
#include <algorithm>
#include <fstream>
#include <sstream>
#include <algorithm>

#include "../Logger.h"
#include "../RequestDispatcher.h"
#include "../Utils.h"


char RESPONSE_HEADER[] =
"HTTP/1.1 200 OK\r\n"
"Content-Type: text/html; charset=UTF-8\r\n"
"Connection: keep-alive\r\n"
"Webpage Content\r\n";

char RESPONSE_CONTENT_HEADER[] =
"<html>"
"<head>Default response</head>"
"<body>"
"<table border=\"1\">";


char RESPONSE_CONTENT_FOOTER[] =
"</table>"
"</body>"
"</html>";


#ifdef WIN32
#include <Windows.h>
#endif

std::string getCurrentFolder()
{
#ifdef WIN32
	TCHAR pchBuffer[256];
	DWORD dwLength = GetCurrentDirectory( 256, pchBuffer );

	std::string ret;
	return ret;
#endif
}

FakeRequestHandler::FakeRequestHandler()
{
}

FakeRequestHandler::~FakeRequestHandler()
{
	// TODO: check is the working thread avilve and stop it if necessary
}

void FakeRequestHandler::Init( const ConfigHelperPtr & config,
								RequestDispatcher * requestDispatcher, 
								std::function<void( std::unique_ptr<ResponseData> )> responseCB )
{
	m_queueManager = requestDispatcher;
	m_responseCallback = responseCB;
}

void FakeRequestHandler::start()
{
	std::thread t( [this] () { this->threadJob(); } );
	m_workThread.swap( t );
}

void FakeRequestHandler::stop()
{
	m_queueManager->stopWaiting();
	m_workThread.join();
}

void FakeRequestHandler::threadJob()
{
	while ( true )
	{
		try {

			// Waitinig for the next request from the queue
			RequestData * request = m_queueManager->scheduleNextRequest();
			//DEBUG_LOG_F << "Starting request processing [ id = " << request->id << " ]";

			//Here's the next request - let's send new response

			ResponsePtr response( new ResponseData );
			response->id = request->id;
			response->data = createResponse( request );

			m_responseCallback( std::move( response ) );
		}
		catch ( cTerminationException exTerm )
		{
			DEBUG_LOG_F << "Terminating job";
			return;
		}
		catch ( std::exception ex )
		{
			DEBUG_LOG_F << "Exception: " << ex.what();
			return;
		}
	}
}

std::vector<char> FakeRequestHandler::createResponse( const RequestData * request ) const
{
	std::stringstream bufferContent;
	bufferContent 
		<< RESPONSE_CONTENT_HEADER
		<< "<tr><td>id</td><td>" << request->id << "</td></tr>"
		<< "<tr><td>http method</td><td>" << getHttpMethodString( request->http_method ) << "</td></tr>"
		<< "<tr><td>address</td><td>" << request->address << "</td></tr>";

	for ( auto param : request->paramsMap )
	{
		bufferContent << "<tr><td>" << param.first << "</td><td>" << param.second << "</td></tr>";
	}

	bufferContent << RESPONSE_CONTENT_FOOTER;

	size_t contentLength = bufferContent.str().size();


	std::stringstream buffer;

	buffer << RESPONSE_HEADER;
	buffer << "Content-Length: " << contentLength << "\r\n";
	
	buffer << "\r\n";
	buffer << bufferContent.str();

	return sstreamToVector( buffer );
}

std::vector<char> FakeRequestHandler::createFaultResponse( RequestIdType id, enErrorIdType err ) const
{
	std::stringstream buffer;

	buffer << RESPONSE_HEADER;

	buffer << "<tr><td>id</td><td>" << id << "</td></tr>";
	
	switch ( err )
	{
	case enErrorIdType::ERR_PARSE_METDHOD:
		buffer << "<tr><td>Error</td><td>Failed to parse http method</td></tr>";
		break;
	}

	buffer << RESPONSE_CONTENT_FOOTER;

	std::string str = buffer.str();

	//DEBUG_LOG_F << str;

	return sstreamToVector( buffer );
}