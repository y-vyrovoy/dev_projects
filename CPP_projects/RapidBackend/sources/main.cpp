#include "stdafx.h"

#include "ServerFrame.h"

int main(int argc, char** argv)
{
    ServerFrame server;
    
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

