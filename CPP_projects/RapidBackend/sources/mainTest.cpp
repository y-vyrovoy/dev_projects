#include "stdafx.h"

#include <chrono>
#include <thread>
#include <mutex>

#include "ServerFramework.h"
#include "MessageException.h"
#include "RequestDispatcher.h"

#include "WaitSentQueue.h"
#include "Logger.h"

#define N_SOCKETS 3

RequestDispatcher g_dispatcher;

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

void pushRequestThreadFunc(const std::chrono::milliseconds & sleepDuration)
{
	while ( !g_stop )
	{
		RequestPtr request = getRequest( g_value );

		size_t oldW = g_dispatcher.waitingRequestCount();
		size_t oldS = g_dispatcher.sentRequestCount();
		std::string tempAddr = request->address;

		g_dispatcher.registerRequest( ( g_value++ ) % N_SOCKETS, std::move( request ) );

		COUT_LOG << " [th id " << std::this_thread::get_id() << " RegReq ]:"
					<< " REQ:"
					<< " register"
					<< " adr: " << tempAddr
					<< " W [" << oldW << " -> " << g_dispatcher.waitingRequestCount() << "]"
					<< " S [" << oldS << " -> " << g_dispatcher.sentRequestCount() << "]";
   
		std::this_thread::sleep_for( sleepDuration );
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


		RequestData * request = g_dispatcher.scheduleNextRequest();
		if( !request )
		{
			continue;
		}


		COUT_LOG << " [th id " << std::this_thread::get_id() << " PullReq_" << funcId << " ]: "
					<< " REQ:"
					<< " pull"
					<< " id: " << request->id
					<< " adr: " << request->address
					<< " W [" << oldW << " -> " << g_dispatcher.waitingRequestCount() << "]"
					<< " S [" << oldS << " -> " << g_dispatcher.sentRequestCount() << "]";


		std::this_thread::sleep_for( sleepDuration );


		size_t oldReps = g_dispatcher.responsesCount();
		size_t oldQ = g_dispatcher.responsesQueueCount();


		ResponsePtr response = std::make_unique<ResponseData>();
		response->id = request->id;

		g_dispatcher.registerResponse( std::move( response ) );


		COUT_LOG << " [th id " << std::this_thread::get_id() << " PullReq_" << funcId << " ]: "
					<< " RESP:"
					<< " registring response"
					<< " id: " << request->id
					<< " resps [" << oldReps << " -> " << g_dispatcher.responsesCount() << "]"
					<< " respQ [" << oldQ << " -> " << g_dispatcher.responsesQueueCount() << "]";

		try 
		{
			{
				std::unique_lock<std::mutex> lock( g_coutMutex );
				g_dispatcher.Dump();
			}
		}
		catch ( std::exception & ex )
		{
			COUT_LOG << __func__ << ": " << "exception: " << ex.what();
		}
	}
}

static unsigned int g_pullRespCount = 0;
void pullResponseThreadFunc(  const std::chrono::milliseconds & sleepDuration  )
{
	unsigned int funcId = g_pullRespCount++;

	while( !g_stop )
	{
		COUT_LOG << " [th id " << std::this_thread::get_id() << " pullResp_" << funcId << " ]:"
					<< " RESP: Waiting for response";
		
		size_t oldReps = g_dispatcher.responsesCount();
		size_t oldQ = g_dispatcher.responsesQueueCount();

		ResponseData * p = g_dispatcher.pullResponse();
		auto id = p->id;


		COUT_LOG << " [th id " << std::this_thread::get_id() << " pullResp ]:"
					<< " RESP:"
					<< " pulling response"
					<< " id: " << id
					<< " resps [" << oldReps << " -> " << g_dispatcher.responsesCount() << "]"
					<< " respQ [" << oldQ << " -> " << g_dispatcher.responsesQueueCount() << "]";

		g_dispatcher.remove( id );

		std::this_thread::sleep_for( sleepDuration );

	}
}

void testAsync()
{
	g_stop = false;
	
	std::thread thReqIn1( pushRequestThreadFunc, 190ms );

	std::thread thReqOut1( pullRequestsThreadFunc, 320ms );
	std::thread thReqOut2( pullRequestsThreadFunc, 370ms );
	std::thread thReqOut3( pullRequestsThreadFunc, 350ms );
	
	std::thread thRespOut1( pullResponseThreadFunc, 500ms );
	std::thread thRespOut2( pullResponseThreadFunc, 520ms );
	   
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
	thReqOut3.join();
	
	thRespOut1.join();
	thRespOut2.join();
}

void testSync()
{
	RequestPtr request = getRequest( g_value );
	g_dispatcher.registerRequest( ( g_value++ ) % N_SOCKETS, std::move( request ) );

	COUT_LOG << __func__<< " [th id " << std::this_thread::get_id() << "]:"
				<< " REQ:" 
				<< " register"
				<< " W [" << g_dispatcher.waitingRequestCount() << "]"
				<< " S [" << g_dispatcher.sentRequestCount() << "]";

	ResponsePtr response = std::make_unique<ResponseData>();
	response->id = 0;


	COUT_LOG << "th id " << std::this_thread::get_id() << ":"
				<< " RESP:"
				<< " registring response"
				<< " id: " << response->id
				<< " responses [" << g_dispatcher.responsesCount() << "]"
				<< " respQueue [" << g_dispatcher.responsesQueueCount() << "]";

	g_dispatcher.registerResponse( std::move( response ) );

	ResponseData * p = g_dispatcher.pullResponse();
	auto respId = p->id;

	COUT_LOG << "th id " << std::this_thread::get_id() << ":"
				<< " RESP:"
				<< " pulling response"
				<< " id: " << respId
				<< " responses [" << g_dispatcher.responsesCount() << "]"
				<< " respQueue [" << g_dispatcher.responsesQueueCount() << "]";
}


void testOne()
{
	RequestDispatcher disp;

	disp.registerRequest(0, getRequest( g_value++ ) );
	disp.registerRequest(1, getRequest( g_value++ ) );
	disp.registerRequest(0, getRequest( g_value++ ) );
	disp.registerRequest(1, getRequest( g_value++ ) );
	disp.registerRequest(0, getRequest( g_value++ ) );
	disp.registerRequest(1, getRequest( g_value++ ) );

	disp.Dump();

	COUT_LOG << "LET'S RUN !" << std::endl 
				<< std::endl 
				<< " ------ +++++++++++++++++ -----------" 
				<< std::endl;

	try
	{
		RequestData * pRequest = disp.scheduleNextRequest();
		auto id1 = pRequest->id;

		COUT_LOG << "sending request #" << id1 << std::endl
					<< "-----------------------" << std::endl;	
		disp.Dump();




		pRequest = disp.scheduleNextRequest();
		auto id2 = pRequest->id;

		COUT_LOG << "sending request #" << id2 << std::endl
					<< "-----------------------" << std::endl;	
		disp.Dump();





		disp.rescheduleRequest( id1 );

		COUT_LOG << "rescheduling request #" << id1 << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();



		pRequest = disp.scheduleNextRequest();
		id1 = pRequest->id;
		
		COUT_LOG << "sending request #" << id1 << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();




		disp.remove( id2 );

		COUT_LOG << "removing request #" << id2 << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();





		pRequest = disp.scheduleNextRequest();
		auto id3 = pRequest->id;
		
		COUT_LOG << "sending request #" << id3 << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();




		ResponsePtr response0 = std::make_unique<ResponseData>(ResponseData());
		response0->id = id1;
		disp.registerResponse( std::move(response0) );

		COUT_LOG << "Register response id #" << id1 << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();





		try
		{
			ResponsePtr response2 = std::make_unique<ResponseData>();
			response2->id = id2;
			disp.registerResponse( std::move( response2 ) );

			COUT_LOG << "Register response id #" << id2 << std::endl
						<< "-----------------------" << std::endl;
			disp.Dump();
		}
		catch ( std::exception & ex )
		{
			COUT_LOG << "Exception. Trying to register response id #" << id2 << ": " << ex.what() ;
		}


		ResponsePtr response3 = std::make_unique<ResponseData>();
		response3->id = id3;
		disp.registerResponse(std::move(response3));

		COUT_LOG << "Register response id #" << id3 << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();


		ResponseData * p = disp.pullResponse();
		auto respID = p->id;

		COUT_LOG << "Pull response # " << respID << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();




		disp.syncPutTopResponseToQueue( disp.getSocket(respID) );
		COUT_LOG << "Response failed. Let's try to send it again. putTopResponseToQueue(" << respID << ")" << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();
		
		disp.remove( respID );
		
		COUT_LOG << "Response succeeded. removeRequestAndResponse(" << respID << ")" << std::endl
					<< "-----------------------" << std::endl;
		disp.Dump();
	}
	catch (std::exception & ex)
	{
		std::cout << "Exception: " << ex.what() << std::endl;
	}

}

void testWaitSentMap()
{
	WaitSentQueue<int> m;

	m.push( 12 );
	m.push( 1 );
	m.push( 105 );

	int a = 19;
	m.push( a );


	try
	{
		int t = m.moveNextToSent();
		m.moveToWaiting( t );

		t = m.moveNextToSent();
		m.remove( t );
	}
	catch ( std::exception & ex )
	{
		std::cout << "Exception: " << ex.what() << std::endl;
	}
		
}

void mainTest(  )
{
	fileLogger::initStaticInstance( "d:\\rb_log.txt" );

	//testOne();
	//testSync();
	testAsync();

	//testWaitSentMap();
}
