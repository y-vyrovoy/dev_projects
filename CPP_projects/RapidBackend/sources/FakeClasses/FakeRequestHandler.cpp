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

#define STD_RESPONSE_FILE_PATH	"..//html//std_response.html"

char RESPONSE_HEADER[] =
"<html>\n"
"<head>Default response</head>\n"
"<body>\n"
"<table border=\"1\">\n";


char RESPONSE_FOOTER[] =
"</table>\n"
"</body>\n"
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

void FakeRequestHandler::Init( RequestDispatcher * pQueueManager,
	std::function<void( std::unique_ptr<ResponseData> )> responseCB )
{
	m_queueManager = pQueueManager;
	m_responseCallback = responseCB;

	try
	{
		std::ifstream file;
		file.open( STD_RESPONSE_FILE_PATH );


		std::string response;
		std::string nextLine;

		while ( std::getline( file, nextLine ) )
		{
			response += nextLine + '\n';
		}

		m_standardResponse.reserve( response.length() + 1 );
		m_standardResponse.resize( response.length() + 1 );
		memcpy( m_standardResponse.data(), response.data(), response.length() );
		m_standardResponse.data()[response.length()] = 0;
		m_standardResponse.resize( response.length() + 1 );

		file.close();
	}
	catch ( const std::exception & ex )
	{
		ERROR_LOG_F << "Can't read standard reposnse file. Exception message: " << ex.what();
	}
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
			DEBUG_LOG_F << "Starting request processing: id=" << request->id;

			//Here's the next request - let's send new response

			ResponsePtr response( new ResponseData );
			response->id = request->id;
			//response->data = m_standardResponse;
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

//TODO: move the function to string_utils lib
std::vector<char> sstreamToVector( std::stringstream& src )
{
	std::vector<char> dst;
	dst.reserve( static_cast< size_t >( src.tellp() ) );
	std::copy( std::istreambuf_iterator<char>( src ),
				std::istreambuf_iterator<char>(),
				std::back_inserter( dst ) );
	return dst;
}

std::vector<char> FakeRequestHandler::createResponse( const RequestData * request ) const
{
	std::stringstream buffer;

	buffer << RESPONSE_HEADER;

	buffer << "<tr>"
		<< "<td>id</td><td>" << request->id << "</td>\n"
		<< "<td>http method</td><td>" << getHttpMethodString( request->http_method ) << "</td>\n"
		<< "<td>address</td><td>" << request->address << "</td>\n";

	for ( auto param : request->paramsMap )
	{
		buffer << "<tr><td>" << param.first << "</td><td>" << param.second << "</td></tr>\n";
	}

	buffer << "</tr>";
	buffer << RESPONSE_FOOTER << '\0';

	std::string str = buffer.str();

	COUT_LOG_F << str;

	return sstreamToVector( buffer );
}

