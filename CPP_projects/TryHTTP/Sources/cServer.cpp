#include "cServer.h"
#include <iostream>
#include <fstream>
#include <memory>
#include <cstring>
#include <algorithm>
#include <vector>
#include <sstream>

#include <ctime>
#include <time.h>
#include <iomanip>
#include <sched.h>



#include "LogMacro.h"
#include "cRequestProcessor.h"

cServer::cServer()
{
    m_NCores = std::thread::hardware_concurrency();
    m_NPullThreads = (m_NCores > 1) ? m_NCores - 1 : 1;
}


int cServer::Init()
{
    cSocketListener::enInitRet nRetVal = m_sockListener.Init();

    if (nRetVal == cSocketListener::enInitRet::INIT_OK)
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
    auto lambda = [this](const REQEST_DATA & reqData)
                        {
                            this->ListenerCallback(reqData);
                        };

    m_sockListener.StartListener(lambda);
    
    for (int i = 0; i < m_NPullThreads; i++)
    {
        {
            m_vecResponseThreads.push_back(std::thread([this](){this->ResponserJob();}));
        }
    }

}

void cServer::ListenerCallback(const REQEST_DATA & reqData)
{
    std::cout << "Request:" << std::endl
                << reqData.vecRequestBuffer.data() << std::endl;

    std::stringstream ss;
    std::chrono::high_resolution_clock::time_point tp = std::chrono::high_resolution_clock::now();
    std::time_t t = std::chrono::high_resolution_clock::to_time_t(tp);
    ss << "logs/" << std::put_time(std::localtime(&t), "%Y_%a_%b_%d_%H:%M:%S")<< ".log";

    std::ofstream ofs;
    ofs.open(ss.str());
    ofs << reqData.vecRequestBuffer.data();
    ofs.close();
    
    m_queue.push(reqData);
    COUT_LOG << "Request is in queue" << std::endl;
}

void cServer::ResponserJob()
{
    while (m_queue.is_waiting())
    {
        try
        {
            try
            {
                REQUEST_PARAMS reqParams;
                REQEST_DATA reqData = m_queue.pull();
                                
                if (m_requestParser.ProcessRequest(reqData, reqParams) == 0)
                {
                    std::vector<char> vecResponse;
                    m_requestProcessor.GetResponse(reqParams, vecResponse);
                    m_sockListener.SendResponse(reqParams, vecResponse);
                }

            }
            catch (const cTerminationException & ex)
            {
                std::cout << __func__ << ": " << "cBlockingQueue::pull() was terminated" << std::endl;
                return;
            }
        }
        catch (const std::exception & ex)
        {
            std::cout << __func__ << ": " << "Unknown error. Reason: " << ex.what() << std::endl;
        }
    }

}


void cServer::CloseServer()
{
    m_sockListener.StopListener();
    
    m_queue.stop_waiting();
    
    for (auto& th : m_vecResponseThreads)
    {
        th.join();
    }
}

cServer::~cServer()
{
    
}
