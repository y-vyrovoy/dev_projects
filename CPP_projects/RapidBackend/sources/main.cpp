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
	fileLogger::initStaticInstance( "d:\\temp\\rb_logs\\rb_log.txt" );

	ServerFramework server;

	server.Initialize();
	server.StartServer();

	for (std::string s; std::cin >> s; )
	{
		if (s == "exit")
		break;
	}

	server.StopServer();

	return 0;
}