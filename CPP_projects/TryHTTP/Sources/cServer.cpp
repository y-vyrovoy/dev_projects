#include "cServer.h"
#include <iostream>
#include <fstream>
#include <memory>
#include <cstring>
#include <algorithm>
#include <vector>

#include "LogMacro.h"
#include "cRequestProcessor.h"

cServer::cServer()
{
    
}

cServer::~cServer()
{
    
}

int cServer::Init()
{
    SL_INIT_RESPONSE nRetVal = m_sockListener.Init();

    if (nRetVal == SL_INIT_RESPONSE::INIT_OK)
    {
        COUT_LOG << "Socket listener was initialized successfully" << std::endl;
    }
    else
    {
        COUT_LOG << "Socket listener was not initialized" << std::endl;
    }

    return 0;
}

void cServer::StartServer()
{
    auto lambda = [this](const std::vector<char> & vecMessageBuffer)
                        {
                            std::cout << "Request:" << std::endl
                                        << vecMessageBuffer.data() << std::endl;

                            std::ofstream ofs;
                            ofs.open("request.txt");
                            ofs << vecMessageBuffer.data();
                            ofs.close();

                            REQUEST_DATA reqData;
                            
                            // !!!! ProcessRequest() should create response
                            if (m_requestParser.ProcessRequest(vecMessageBuffer, reqData) == 0)
                            {
                                std::vector<char> vecResponse;
                                m_requestProcessor.GetResponse(reqData, vecResponse);
                                return vecResponse;
                            }
                            
                            std::vector<char> vecReturn(0); 
                            return vecReturn;
                                
    };

    m_sockListener.StartListener(lambda);
}

void cServer::CloseServer()
{
    m_sockListener.StopListener();
}