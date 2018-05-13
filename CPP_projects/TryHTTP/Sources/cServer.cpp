#include "cServer.h"
#include <iostream>

#include <fstream>
#include <memory>


cServer::cServer()
{
	m_pListener = std::make_unique<cSocketListener>();
}

cServer::~cServer()
{
	CloseServer();
}

int cServer::Init()
{
	SL_INIT_RESPONCE nRetVal = m_pListener->Init();

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

	auto lambda = [this](const char * pBuffer, const int & nSize)
					{
						std::cout << "Request:" << std::endl
									<< pBuffer << std::endl;

						std::ofstream ofs;
						ofs.open("request.txt");
						ofs << pBuffer;
						ofs.close();

						REQUEST_DATA reqData;
						m_requestProcessor.ProcessRequest(pBuffer, nSize, reqData);
					};


	m_pListener->StartListener(lambda);
}

void cServer::CloseServer()
{
	m_pListener->StopListener();
}

