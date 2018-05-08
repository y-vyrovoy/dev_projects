#include "cServer.h"
#include <iostream>


cServer::cServer()
{
}

cServer::~cServer()
{
	CloseServer();
}

int cServer::Init()
{
	SL_INIT_RESPONCE nRetVal = m_Listener.Init();

	if (nRetVal == SL_INIT_RESPONCE::INIT_OK)
	{
		std::cout << "Socket listener was initialized successfully" << std::endl;
	}
	else
	{
		std::cout << "Socket listener was not initialized" << std::endl;
	}

    return 0;
}

void cServer::StartServer()
{
	m_Listener.StartListener(true);
}

void cServer::CloseServer()
{
	m_Listener.StopListener();
}

