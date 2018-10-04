#include "stdafx.h"

#include <chrono>
#include <thread>
#include <mutex>

#include "ServerFramework.h"
#include "MessageException.h"
#include "ResponseDispatcher.h"

#define N_SOCKETS 3

ResponseDispatcher g_dispatcher;

std::atomic<bool> g_stop;
std::atomic<unsigned int> g_value;

std::mutex	g_coutMutex;




using namespace std::chrono_literals;

RequestPtr getRequest( unsigned int id )
{
	std::stringstream buf;
	buf << "[ request " << id << " ]";

	RequestPtr request = std::make_unique<RequestData>();
	request->address = buf.str();

	return request;
}

void pushRequestThreadFunc()
{
	while ( !g_stop )
	{
		RequestPtr request = getRequest( g_value );

		size_t oldW = g_dispatcher.waitingRequestCount();
		size_t oldS = g_dispatcher.sentRequestCount();
		std::string tempAddr = request->address;

		g_dispatcher.registerRequest( ( g_value++ ) % N_SOCKETS, std::move( request ) );

		{
			std::unique_lock<std::mutex> lock( g_coutMutex );
			std::cout << " [th id " << std::this_thread::get_id() << " RegReq ]:"
						<< " REQ:" 
						<< " register"
						<< " adr: " << tempAddr
						<< " W [" << oldW << " -> " << g_dispatcher.waitingRequestCount() << "]"
						<< " S [" << oldS << " -> "  << g_dispatcher.sentRequestCount() << "]"
						<< std::endl;
		}
   
		std::this_thread::sleep_for( 20ms );
	}
}

static unsigned int g_pullReqCount = 0;

void pullRequestsThreadFunc( const std::chrono::milliseconds & sleepDuration )
{
	unsigned int funcId = g_pullReqCount++;

	while ( !g_stop )
	{
		size_t oldW = g_dispatcher.waitingRequestCount();
		size_t oldS = g_dispatcher.sentRequestCount();


		RequestData * request = g_dispatcher.getNextRequestAndWait();
		if( !request )
		{
			continue;
		}

		{
			std::unique_lock<std::mutex> lock( g_coutMutex );
			std::cout << " [th id " << std::this_thread::get_id() << " PullReq_" << funcId << " ]: "
						<< " REQ:" 
						<< " pull"
						<< " id: " << request->id
						<< " adr: " << request->address
						<< " W [" << oldW << " -> " << g_dispatcher.waitingRequestCount() << "]"
						<< " S [" << oldS << " -> "  << g_dispatcher.sentRequestCount() << "]"
						<< std::endl;
		}

		std::this_thread::sleep_for( sleepDuration );


		size_t oldReps = g_dispatcher.responsesCount();
		size_t oldQ = g_dispatcher.responsesQueueCount();


		ResponsePtr response = std::make_unique<ResponseData>();
		response->id = request->id;

		g_dispatcher.registerResponse( std::move( response ) );

		{
			std::unique_lock<std::mutex> lock( g_coutMutex );
			std::cout << " [th id " << std::this_thread::get_id() << " PullReq_" << funcId << " ]: "
				<< " RESP:"
				<< " registring response"
				<< " id: " << request->id
				<< " resps [" << oldReps << " -> "  << g_dispatcher.responsesCount() << "]"
				<< " respQ [" << oldQ << " -> " << g_dispatcher.responsesQueueCount() << "]"
				<< std::endl;
		}

		try 
		{
			{
				std::unique_lock<std::mutex> lock( g_coutMutex );
				g_dispatcher.Dump();
			}
		}
		catch ( std::exception & ex )
		{
			std::cout << __func__ <<  ": " << "exception: " << ex.what() << std::endl;
		}
	}
}


void pullResponseThreadFunc()
{
	while( !g_stop )
	{
		{
			std::unique_lock<std::mutex> lock( g_coutMutex );

			std::cout << " [th id " << std::this_thread::get_id() << " pullResp ]:"
				<< " RESP: Waiting for response"
				<< std::endl;
		}
		
		size_t oldReps = g_dispatcher.responsesCount();
		size_t oldQ = g_dispatcher.responsesQueueCount();

		ResponseData * p = g_dispatcher.pullResponse();
		auto id = p->id;

		{
			std::unique_lock<std::mutex> lock( g_coutMutex );

			std::cout << " [th id " << std::this_thread::get_id() << " pullResp ]:"
						<< " RESP:"
						<< " pulling response"
						<< " id: " << id
						<< " resps [" << oldReps << " -> "  << g_dispatcher.responsesCount() << "]"
						<< " respQ [" << oldQ << " -> " << g_dispatcher.responsesQueueCount() << "]"
						<< std::endl;
		}


	}
}

void testAsync()
{
	g_stop = false;
	
	std::thread thReqIn1( pushRequestThreadFunc );

	std::thread thReqOut1( pullRequestsThreadFunc, 50ms );
	std::thread thReqOut2( pullRequestsThreadFunc, 35ms );
	
	std::thread thRespOut1( pullResponseThreadFunc );
	   
	for (std::string s; std::cin >> s; )
	{
		if ( s == "exit" )
		{
			g_stop = true;
			break;
		}
	}
	
	thReqIn1.join();

	thReqOut1.join();
	thReqOut2.join();
	
	thRespOut1.join();
}

void testSync()
{
	RequestPtr request = getRequest( g_value );
	g_dispatcher.registerRequest( ( g_value++ ) % N_SOCKETS, std::move( request ) );

	std::cout << __func__<< " [th id " << std::this_thread::get_id() << "]:"
				<< " REQ:" 
				<< " register"
				<< " W [" << g_dispatcher.waitingRequestCount() << "]"
				<< " S [" << g_dispatcher.sentRequestCount() << "]"
				<< std::endl;

	ResponsePtr response = std::make_unique<ResponseData>();
	response->id = 0;


	std::cout << "th id " << std::this_thread::get_id() << ":"
		<< " RESP:"
		<< " registring response"
		<< " id: " << response->id
		<< " responses [" << g_dispatcher.responsesCount() << "]"
		<< " respQueue [" << g_dispatcher.responsesQueueCount() << "]"
		<< std::endl;

	g_dispatcher.registerResponse( std::move( response ) );

	ResponseData * p = g_dispatcher.pullResponse();
	auto respId = p->id;

	std::cout << "th id " << std::this_thread::get_id() << ":"
				<< " RESP:"
				<< " pulling response"
				<< " id: " << respId
				<< " responses [" << g_dispatcher.responsesCount() << "]"
				<< " respQueue [" << g_dispatcher.responsesQueueCount() << "]"
				<< std::endl;

}


void testOne()
{
	ResponseDispatcher disp;

	disp.registerRequest(0, getRequest( g_value++ ) );
	disp.registerRequest(1, getRequest( g_value++ ) );
	disp.registerRequest(0, getRequest( g_value++ ) );
	disp.registerRequest(1, getRequest( g_value++ ) );
	disp.registerRequest(0, getRequest( g_value++ ) );
	disp.registerRequest(1, getRequest( g_value++ ) );

	disp.Dump();

	std::cout << "LET'S RUN !" << std::endl 
				<< std::endl 
				<< " ------ +++++++++++++++++ -----------" 
				<< std::endl 
				<< std::endl;

	try
	{
		RequestData * pRequest = disp.getNextRequest();
		auto id1 = pRequest->id;

		std::cout << "sending request #" << id1 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;	
		disp.Dump();




		pRequest = disp.getNextRequest();
		auto id2 = pRequest->id;

		std::cout << "sending request #" << id2 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;	
		disp.Dump();





		disp.rescheduleRequest( id1 );

		std::cout << "rescheduling request #" << id1 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();



		pRequest = disp.getNextRequest();
		id1 = pRequest->id;
		
		std::cout << "sending request #" << id1 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;	
		disp.Dump();




		disp.removeRequest( id2 );

		std::cout << "removing request #" << id2 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;	
		disp.Dump();





		pRequest = disp.getNextRequest();
		auto id3 = pRequest->id;
		
		std::cout << "sending request #" << id3 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;	
		disp.Dump();




		ResponsePtr response0 = std::make_unique<ResponseData>(ResponseData());
		response0->id = id1;
		disp.registerResponse( std::move(response0) );

		std::cout << "Register response id #" << id1 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;	
		disp.Dump();





		try
		{
			ResponsePtr response2 = std::make_unique<ResponseData>();
			response2->id = id2;
			disp.registerResponse( std::move( response2 ) );

			std::cout << "Register response id #" << id2 << std::endl;
			std::cout << "-----------------------" << std::endl << std::endl;
			disp.Dump();
		}
		catch ( std::exception & ex )
		{
			std::cout << "Exception. Trying to register response id #" << id2 << ": " << ex.what() << std::endl;
		}


		ResponsePtr response3 = std::make_unique<ResponseData>();
		response3->id = id3;
		disp.registerResponse(std::move(response3));

		std::cout << "Register response id #" << id3 << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();


		ResponseData * p = disp.pullResponse();
		auto respID = p->id;

		std::cout << "Pull response # " << respID << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();




		disp.putTopResponseToQueue( disp.getSocket(respID) );
		std::cout << "Response failed. Let's try to send it again. putTopResponseToQueue(" << respID << ")" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();
		
		disp.removeRequestAndResponse( respID );
		
		std::cout << "Response succeeded. removeRequestAndResponse(" << respID << ")" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();
	}
	catch (std::exception & ex)
	{
		std::cout << "Exception: " << ex.what() << std::endl;
	}

}

int main( int argc, char** argv )
{
	//testOne();
	//testSync();
	testAsync();
}

//int main(int argc, char** argv)
//{
//	ServerFramework server;
//
//	server.Initialize();
//	server.StartServer();
//
//	for (std::string s; std::cin >> s; )
//	{
//		if (s == "exit")
//		break;
//	}
//
//	server.StopServer();
//
//	return 0;
//}


