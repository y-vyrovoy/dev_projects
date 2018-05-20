#include "cServer.h"
#include <iostream>

#include <fstream>
#include <memory>
#include <cstring>
#include <algorithm>
#include <vector>

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
    SL_INIT_RESPONSE nRetVal = m_pListener->Init();

    if (nRetVal == SL_INIT_RESPONSE::INIT_OK)
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
    InitFakeResponse();
    
    auto lambda = [this](const std::vector<char> & vecMessageBuffer,
                            //const char * pBuffer, const int & nSize, 
                            char * pchResponseBuffer, int & nResponseSize)
                        {
                            std::cout << "Request:" << std::endl
                                        << vecMessageBuffer.data() << std::endl;

                            std::ofstream ofs;
                            ofs.open("request.txt");
                            ofs << vecMessageBuffer.data();
                            ofs.close();

                            REQUEST_DATA reqData;
                            
                            // !!!! ProcessRequest() should create response
                            m_requestProcessor.ProcessRequest(vecMessageBuffer, reqData);
                            
                            int nSend = std::min(nResponseSize, static_cast<int>( m_vecResponceBuffer.size()));
                            memcpy(pchResponseBuffer, m_vecResponceBuffer.data(), nSend);
                            nResponseSize = nSend;
                            
    };

    m_pListener->StartListener(lambda);
}

void cServer::CloseServer()
{
    m_pListener->StopListener();
}

void delDel(char * p)
{
    delete [] p;
}

void cServer::InitFakeResponse()
{
    try
    {
        std::ifstream ifs;
        ifs.open("response.txt", std::ios::in | std::ios::ate | std::ios::binary);
        int nFileSize = ifs.tellg();
        ifs.seekg(0, ifs.beg);

        if (nFileSize <= 0)
        {
            std::cout << "File length is " << nFileSize << std::endl;
            return;
        }

        m_vecResponceBuffer.resize(nFileSize);
        

        ifs.seekg (0, std::ios::beg);
        ifs.read (m_vecResponceBuffer.data(), nFileSize);
        ifs.close();
        
        std::cout << " -------- response.txt -------- " << std::endl;
        std::cout << m_vecResponceBuffer.data() << std::endl;
        std::cout << " -------- response.txt -------- " << std::endl;
        std::cout << std::endl;        
    }
    catch (const std::exception & ex)
    {
        std::cout << "InitFakeResponse() error: " << ex.what() << std::endl; 
    }
}