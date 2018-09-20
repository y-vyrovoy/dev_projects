#include "stdafx.h"

#include <chrono>
#include <thread>

#include "ServerFramework.h"
#include "MessageException.h"
#include "ResponseDispatcher.h"

#define N_SOCKETS 4

ResponseDispatcher g_dispatcher;

std::atomic<bool> g_stop;
std::atomic<unsigned int> g_value;

using namespace std::chrono_literals;

void pushRequestThreadFunc()
{
    auto threadID = std::this_thread::get_id();
    for (int i = 0; i < 20; ++i)
    {
        DEBUG_LOG << "push" 
                    " [ thread: " << threadID << " ]"
                    " [ request " << g_value << " ]" 
                << std::endl;
        
        g_dispatcher.registerRequest( ( g_value++ ) % N_SOCKETS );
        
        std::this_thread::sleep_for(50ms);
    }
}

void pushResponseThreadFunc()
{

}


void pullThreadFunc()
{
    
    while( !g_stop )
    {
        ResponseData * p = g_dispatcher.getTopResponse();
        auto id = p->id;

        DEBUG_LOG << "Pulling response # " << id;

        if ( id % 7 != 0 )
        {
            g_dispatcher.removeResponse( id );
        }
        else
        {
            g_dispatcher.putTopOfChainToQueue( g_dispatcher.getSocket( id ) );
        }
        
        std::this_thread::sleep_for(20ms);
    }
}


int main(int argc, char** argv)
{
    g_stop = false;
    
    std::thread thIn1( pushRequestThreadFunc );
    //std::thread thIn2( pushThreadFunc );
    
    std::thread thOut( pullThreadFunc );
       
    for (std::string s; std::cin >> s; )
    {
            if (s == "exit")
            break;
    }
    
    thIn1.join();
    //thIn2.join();
    
    thOut.join();
}

//int main(int argc, char** argv)
//{
//	ResponseDispatcher disp;
//
//	disp.registerRequest(0);
//	disp.registerRequest(1);
//	disp.registerRequest(2);
//	disp.registerRequest(0);
//	disp.registerRequest(1);
//	disp.registerRequest(2);
//	disp.registerRequest(0);
//	disp.registerRequest(1);
//
//	disp.Dump();
//
//	std::cout << "LET'S RUN !" << std::endl 
//				<< std::endl 
//				<< " ------ +++++++++++++++++ -----------" 
//				<< std::endl 
//				<< std::endl;
//
//	try
//	{
//		ResponsePtr response0 = std::make_unique<ResponseData>(ResponseData());
//		response0->id = 0;
//		disp.registerResponse( std::move(response0) );
//
//		std::cout << "Register response id #0" << std::endl;
//		std::cout << "-----------------------" << std::endl << std::endl;	
//		disp.Dump();
//
//
//		ResponsePtr response3 = std::make_unique<ResponseData>(ResponseData());
//		response3->id = 3;
//		disp.registerResponse(std::move(response3));
//
//		std::cout << "Register response id #3" << std::endl;
//		std::cout << "-----------------------" << std::endl << std::endl;
//		disp.Dump();
//
//		ResponsePtr response6 = std::make_unique<ResponseData>(ResponseData());
//		response6->id = 6;
//		disp.registerResponse(std::move(response6));
//
//		std::cout << "Register response id #6" << std::endl;
//		std::cout << "-----------------------" << std::endl << std::endl;
//		disp.Dump();
//
//
//		ResponseData * p = disp.getTopResponse();
//		std::cout << "Pull response # " << p->id << std::endl;
//		std::cout << "-----------------------" << std::endl << std::endl;
//		disp.Dump();
//
//		disp.putTopOfChainToQueue( disp.getSocket(0) );
//		std::cout << "putTopOfChainToQueue(0)" << std::endl;
//		std::cout << "-----------------------" << std::endl << std::endl;
//		disp.Dump();
//
//		disp.getTopResponse();
//		disp.removeResponse(0);
//		std::cout << "pullResponse() -> removeResponse(0)" << std::endl;
//		std::cout << "-----------------------" << std::endl << std::endl;
//		disp.Dump();
//	}
//	catch (std::exception & ex)
//	{
//		std::cout << "Exception: " << ex.what() << std::endl;
//	}
//
//}

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


