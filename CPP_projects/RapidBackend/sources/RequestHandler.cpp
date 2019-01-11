#include "stdafx.h"
#define NOMINMAX

#include "RequestHandler.h"

#include <cstring>
#include <algorithm>


#include "RequestDispatcher.h"
#include "Utils.h"
#include "Logger.h"

RequestHandler::RequestHandler()
{
}

RequestHandler::~RequestHandler()
{
	// TODO: check is the working thread avilve and stop it if necessary
}

void RequestHandler::Init( RequestDispatcher * pRequestDispatcher,
							std::function<void(std::unique_ptr<ResponseData>)> responseCB)
{
	m_queueManager = pRequestDispatcher;
	m_responseCallback = responseCB;
}

void RequestHandler::start()
{
	std::thread t([this]() { this->threadJob(); });
	m_workThread.swap(t);
}

void RequestHandler::stop()
{
	m_queueManager->stopWaiting();
	m_workThread.join();
}

void RequestHandler::threadJob()
{
	while (true)
	{
		try {

			// Waitinig for the next request from the queue
			RequestData * request = m_queueManager->scheduleNextRequest();
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


std::vector<char> RequestHandler::createFaultResponse( RequestIdType id, enErrorIdType err ) const
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