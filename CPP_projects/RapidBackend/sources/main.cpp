#include "stdafx.h"

#include <chrono>
#include <thread>
#include <mutex>

#include "ServerFramework.h"
#include "MessageException.h"
#include "RequestDispatcher.h"

#include "WaitSentQueue.h"
#include "Logger.h"


using namespace std::chrono_literals;

int main(int argc, char** argv)
{
	try
	{
		ConfigHelperPtr config( new ConfigHelper );

		config->parseCmdLine( argc, argv );

		std::string logFilename;
		if ( !config->getOptional( "log", logFilename ) )
		{
			std::cout << "Can't find log filename. Terminating" << std::endl;
		}

		fileLogger::initStaticInstance( logFilename );

		ServerFramework server;

		server.Initialize( config );
		server.StartServer();

		for ( std::string s; std::cin >> s; )
		{
			if ( s == "exit" )
				break;
		}

		server.StopServer();
	}
	catch ( const std::exception & ex )
	{
		std::cout << "Program crashed. Error: " << ex.what() << std::endl;
	}

	return 0;
}