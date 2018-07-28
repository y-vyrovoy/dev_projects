#include "stdafx.h"

#include "ServerFramework.h"



int main(int argc, char** argv)
{
	ResponseDispatcher disp;

	disp.registerRequest(0, 0);
	disp.registerRequest(1, 1);
	disp.registerRequest(2, 2);
	disp.registerRequest(3, 0);
	disp.registerRequest(4, 1);
	disp.registerRequest(5, 2);
	disp.registerRequest(6, 0);
	disp.registerRequest(7, 1);

	disp.Dump();

	std::cout << "LET'S RUN !" << std::endl 
				<< std::endl 
				<< " ------ +++++++++++++++++ -----------" 
				<< std::endl 
				<< std::endl;

	try
	{
		ResponsePtr response0 = std::make_unique<ResponseData>(ResponseData());
		response0->id = 0;
		disp.registerResponse( std::move(response0) );

		std::cout << "Register response id #0" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;	
		disp.Dump();


		ResponsePtr response3 = std::make_unique<ResponseData>(ResponseData());
		response3->id = 3;
		disp.registerResponse(std::move(response3));

		std::cout << "Register response id #3" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();

		ResponsePtr response6 = std::make_unique<ResponseData>(ResponseData());
		response6->id = 6;
		disp.registerResponse(std::move(response6));

		std::cout << "Register response id #6" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();


		ResponseData * p = disp.pullResponse();
		std::cout << "Pull response" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();

		disp.putTopResponseToQueue( disp.getSocket(0) );
		std::cout << "putTopResponseToQueue(0)" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();

		disp.pullResponse();
		disp.removeResponse(0);
		std::cout << "pullResponse() -> removeResponse(0)" << std::endl;
		std::cout << "-----------------------" << std::endl << std::endl;
		disp.Dump();
	}
	catch (std::exception & ex)
	{
		std::cout << ex.what() << std::endl;
	}

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


