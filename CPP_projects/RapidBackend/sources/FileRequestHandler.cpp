#include "stdafx.h"
#define NOMINMAX

#include "FileRequestHandler.h"

#include <cstring>
#include <algorithm>


#include "RequestDispatcher.h"
#include "Utils.h"
#include "Logger.h"
#include "ConfigHelper.h"

FileRequestHandler::FileRequestHandler()
{
}

FileRequestHandler::~FileRequestHandler()
{
	// TODO: check is the working thread avilve and stop it if necessary
}

void FileRequestHandler::Init( const ConfigHelperPtr & config,
								RequestDispatcher * requestDispatcher, 
								std::function<void( std::unique_ptr<ResponseData> )> responseCB )
{
	m_config = config;
	m_queueManager = requestDispatcher;
	m_responseCallback = responseCB;

	m_rootFolder = m_config->getRootFolder();
	if ( m_rootFolder.empty() )
	{
		THROW_MESSAGE << "Can't get root folder from config";
	}
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

			DEBUG_LOG_F << "Starting request processing: id=" << request->id;


			//Here's the next request - let's send new response

			ResponsePtr response(new ResponseData);
			response->id = request->id;

			// TODO: getting response from ResponseCompiler
			
			const static char * pchMessage = "Default response message";
			
			memcpy( response->data.data(), pchMessage, std::min( response->data.size(), std::strlen( pchMessage ) + 1 ) );

			m_responseCallback( std::move(response) );
			
		}
		catch (cTerminationException exTerm)
		{
			DEBUG_LOG_F << "Terminating job";
			return;
		}
		catch (std::exception ex)
		{
			DEBUG_LOG_F << "Exception: "<< ex.what();
			return;
		}

	}
}


std::vector<char> FileRequestHandler::createFaultResponse( RequestIdType id, enErrorIdType err ) const
{
	std::stringstream buffer;

	//buffer << RESPONSE_HEADER;

	//buffer
	//	<< "<tr><td>id</td><td>" << id << "</td></tr>";
	//
	//switch ( err )
	//{
	//case enErrorIdType::ERR_PARSE_METDHOD:
	//	buffer << "<tr><td>Failed to parse http method</td></tr>";
	//	break;
	//}

	//buffer << RESPONSE_FOOTER << '\0';

	//std::string str = buffer.str();

	//DEBUG_LOG_F << str;

	return sstreamToVector( buffer );
}