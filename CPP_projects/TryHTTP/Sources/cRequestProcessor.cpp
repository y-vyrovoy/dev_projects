#include "cRequestProcessor.h"

#include <iostream>
#include <fstream>

#include "LogMacro.h"

cRequestProcessor::cRequestProcessor() 
{
    InitFakeResponse();
}

cRequestProcessor::~cRequestProcessor() 
{
}

int cRequestProcessor::GetResponse(const REQUEST_PARAMS & requestData, 
                                    std::vector<char> & vecResponse)
{
    vecResponse = m_vecResponceBuffer;
    return 0;
}

void cRequestProcessor::InitFakeResponse()
{
    COUT_LOG << std::endl;
    
    try
    {
        std::ifstream ifs;
        ifs.open("logs/response.html", std::ios::in | std::ios::ate | std::ios::binary);
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
        
//        std::cout << " -------- response.txt -------- " << std::endl;
//        std::cout << m_vecResponceBuffer.data() << std::endl;
//        std::cout << " -------- response.txt -------- " << std::endl;
//        std::cout << std::endl;        
        
    }
    catch (const std::exception & ex)
    {
        COUT_LOG << "Exception: " << ex.what() << std::endl; 
    }
}

