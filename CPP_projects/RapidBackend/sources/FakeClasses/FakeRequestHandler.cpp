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
#include "../StdResponsesHelper.h"



char ICON_RESPONSE_HEADER[] =
	"HTTP/1.1 200 OK\r\n"
	"Content-Type: text/html; charset=UTF-8\r\n"
	"Connection: keep-alive\r\n"
	"Webpage Content\r\n";

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


FakeRequestHandler::FakeRequestHandler()
{
}

FakeRequestHandler::~FakeRequestHandler()
{
	// TODO: check is the working thread avilve and stop it if necessary
}

void FakeRequestHandler::Init( const ConfigHelperPtr & config,
								StdResponseHelper * stdResponseHelper, 
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

			//Here's the next request - let's send new response

			ResponsePtr response( new ResponseData );
			response->id = request->getId();
			response->data = createResponse( request );

			m_responseCallback( std::move( response ) );
		}
		catch ( cTerminationException & exTerm )
		{
			DEBUG_LOG_F << "Terminating job";
			return;
		}
		catch ( std::exception & ex )
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
		<< BASE_RESPONSE_CONTENT_HEADER
		<< "<tr><td>id</td><td>" << request->getId() << "</td></tr>"
		<< "<tr><td>http method</td><td>" << getHttpMethodString( request->getHTTP_method() ) << "</td></tr>"
		<< "<tr><td>address</td><td>" << request->getAddress() << "</td></tr>";

	for ( auto param : const_cast< RequestData * >( request )->getParamsMap() )
	{
		bufferContent << "<tr><td>" << param.first << "</td><td>" << param.second << "</td></tr>";
	}

	bufferContent << BASE_RESPONSE_CONTENT_FOOTER;

	size_t contentLength = bufferContent.str().size();


	std::stringstream buffer;

	buffer << BASE_RESPONSE_HEADER;
	buffer << "Content-Length: " << contentLength << "\r\n";
	
	buffer << "\r\n";
	buffer << bufferContent.str();

	return Utils::sstreamToVector( buffer );
}

