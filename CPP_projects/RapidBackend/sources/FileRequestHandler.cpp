#include "stdafx.h"
#define NOMINMAX

#include "FileRequestHandler.h"

#include <algorithm>
#include <fstream>		// std::fstream

#include "RequestDispatcher.h"
#include "Utils.h"
#include "Logger.h"
#include "ConfigHelper.h"
#include "StdResponsesHelper.h"


#ifdef _WIN32
	static const char PATH_SEPARATOR = '\\';
	static const char PATH_SEPARATOR_WRONG = '/';
#else
	static const char PATH_SEPARATOR = '/';
	static const char PATH_SEPARATOR_WRONG = '\\';
#endif

FileRequestHandler::FileRequestHandler()
{
}

FileRequestHandler::~FileRequestHandler()
{
	// TODO: check is the working thread alive and stop it if necessary
}

void FileRequestHandler::Init( const ConfigHelperPtr & config,
								StdResponseHelper * stdResponseHelper, 
								RequestDispatcher * requestDispatcher, 
								std::function<void( std::unique_ptr<ResponseData> )> responseCB )
{
	m_config = config;
	m_stdResponseHelper = stdResponseHelper;
	m_queueManager = requestDispatcher;
	m_responseCallback = responseCB;

	m_rootFolder = m_config->getRootFolder();
	if ( m_rootFolder.empty() )
	{
		throw std::runtime_error( "Can't get root folder from config" );
	}

	m_mapTypes["html"] = "text/html; charset=UTF-8";
	m_mapTypes["css"] = "text/css";
	m_mapTypes["jpg"] = "image/jpg";
	m_mapTypes["ico"] = "image/png";
	
}

void FileRequestHandler::start()
{
	std::thread t([this]() { this->threadJob(); });
	m_workThread.swap(t);
}

void FileRequestHandler::stop()
{
	m_queueManager->stopWaiting();
	m_workThread.join();
}

void FileRequestHandler::threadJob()
{
	while (true)
	{
		try {

			// Waitinig for the next request from the queue
			RequestData * request = m_queueManager->scheduleNextRequest();
			if ( !request )
			{
				WARN_LOG_F << "Request dispatched returned empty request. Skipping";
				continue;
			}

			//Here's the next request - let's send new response
			ResponsePtr response( new ResponseData );
			response->id = request->getId();
			response->data = createResponse( request );

			m_responseCallback( std::move( response ) );
		}
		catch ( cTerminationException & exTerm )
		{
			WARN_LOG_F << "Terminating job";
			return;
		}
		catch ( std::exception & ex )
		{
			ERROR_LOG_F << "Failed to create response. Sending Error message: " << ex.what();

			ResponsePtr response( new ResponseData );
			response->data = m_stdResponseHelper->createStdResponse( E500_INT_SERVER_ERROR, "Ooooops" );
			m_responseCallback( std::move( response ) );
			return;
		}
	}
}

static const char APP_CONTENT_TYPE[] = "application";

std::vector<char> FileRequestHandler::createResponse( const RequestData * request ) const
{
	// opening file
	std::string sFilePathname = m_config->getRootFolder() + request->getAddress();

	std::fstream file( sFilePathname, std::ios::in | std::ios::binary );
	if ( !file.is_open() )
	{
		std::string sErrMessage = "Can't open file [" + request->getAddress() + "]";

		WARN_LOG_F << "ReqId [" << request->getId() << "]. " << sErrMessage << ". Sending FAIL RESPONSE ";
		return m_stdResponseHelper->createStdResponse( enResponseId::E404_CANT_FIND_FILE, sErrMessage );
	}

	// !!! TODO: manage cast streamoff -> size_t

	file.seekg (0, std::ios::end);
	auto fileSize = static_cast< size_t >( file.tellg() );
	file.seekg (0, std::ios::beg);


	// adding response header
	std::stringstream buffer;
	buffer << 
		"HTTP/1.1 200 OK\r\n"
		"Connection: keep-alive\r\n"
		"Webpage Content\r\n";


	// adding content type to header
	const char * pType = getContentType( request->getAddress() );
	if ( !pType )
	{
		pType = APP_CONTENT_TYPE;
	}

	buffer << "Content-Type: " << pType << "\r\n";
	buffer << "Content-Length: " << fileSize << "\r\n";
	buffer << "\r\n";


	// !!! TODO: manage cast streamoff -> size_t

	buffer.seekp( 0, std::ios::end );
	auto headerSize = static_cast< size_t >( buffer.tellp() );
	buffer.seekp (0, std::ios::beg);

	std::vector<char> vecFile;
	vecFile.reserve( headerSize + fileSize );

	std::copy( std::istreambuf_iterator<char>( buffer ), std::istreambuf_iterator<char>(), std::back_inserter( vecFile ) );
	std::copy( std::istreambuf_iterator<char>( file ), std::istreambuf_iterator<char>(), std::back_inserter( vecFile ) );

	return vecFile;
}

const char * FileRequestHandler::getContentType( const std::string & filePathname ) const
{
	size_t posLastSlash = filePathname.rfind( '/' );
	if ( posLastSlash == std::string::npos )
		return nullptr;

	size_t posDot = filePathname.rfind( '.' );
	if ( posDot == std::string::npos )
		return nullptr;

	// empty filename
	if ( posLastSlash >= posDot - 1 )
		return nullptr;

	size_t posQuestionMark = filePathname.find( '?' );
	if ( posQuestionMark == std::string::npos )
		posQuestionMark = filePathname.length();

	if ( posDot >= posQuestionMark - 1 )
		return nullptr;

	std::string extension = filePathname.substr( posDot + 1, posQuestionMark - ( posDot + 1 ) );

	auto itType = m_mapTypes.find( extension );
	if ( itType == m_mapTypes.end() )
		return nullptr;

	return itType->second.data();
}