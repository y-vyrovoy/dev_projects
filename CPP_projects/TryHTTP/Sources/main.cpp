#include "cServer.h"

#include <iostream>
#include <string.h>
#include <fstream>
#include <memory>
#include <vector>
#include <map>
#include <string>

#include "LogMacro.h"
#include "cHTTPRequestParser.h"
#include "TimeLib.h"

void testServer()
{
    COUT_LOG << std::endl;

    cServer serv;
    if (serv.Init() < 0)
    {
    	std::cout << "Server could not initialize" << std::endl;
    }
    serv.StartServer();
    
    while(true)
    {
        std::string cmd;
        std::cin >> cmd;
        
        if (cmd == "exit")
        {
            serv.CloseServer();
            return;
        }
    }
}

void testParser()
{
	COUT_LOG << std::endl;

        try
        {
            std::ifstream ifs;
            ifs.open("request.txt", std::ios::in | std::ios::ate | std::ios::binary);
            int nFileSize = ifs.tellg();
            ifs.seekg(0, ifs.beg);

            if (nFileSize <= 0)
            {
                std::cout << "File length is " << nFileSize << std::endl;
                //return;
            }

            
            std::vector<char> vecBuffer(nFileSize);

/*            
            char * pB = new char[nFileSize];
            auto del = [](char * p){delete [] p;};
            std::unique_ptr<char[], decltype(del)> pBuffer = std::unique_ptr<char[], decltype(del)>(pB, del);
*/
            
            ifs.seekg (0, std::ios::beg);
            ifs.read (vecBuffer.data(), nFileSize);
            ifs.close();

            
            
            std::cout << " -------- FILE -------- " << std::endl;
            std::cout << vecBuffer.data() << std::endl;
            std::cout << " -------- FILE -------- " << std::endl;
            std::cout << std::endl;

            cHTTPRequestParser pr;
            REQUEST_DATA reqData;
            pr.ProcessRequest(vecBuffer, reqData);

        }
        catch (std::exception ex)
        {
            std::cout << ex.what() << std::endl;
        }
        


/*
	const char * pchMessage[] = {
									"GET /params/params?200 HTTP/1.1",
									"PUT /params/params?200 HTTP/1.1",
									"HEAD /params/params?200 HTTP/1.1",
									"POST /params/params?200 HTTP/1.1",
									"TRACE / HTTP/1.1",
									"DELETE / HTTP/1.1",
									"CONNECT / HTTP/1.1",
									"OPTIONS / HTTP/1.1",
									"asd"
								};

    int NMethods = sizeof(pchMessage) / sizeof(pchMessage[0]);

    std::cout << "sizeof(pchMessage) = " << NMethods << std::endl;

    REQUEST_DATA requestData;
    for (int i = 0; i < NMethods; i++)
    {
    	std::cout << "pchMessage[" << i << "] = "  << pchMessage[i] << " [Size = " << strlen(pchMessage[i]) << "]" << std::endl;
		cRequestProcessor pr;
		pr.ProcessRequest(static_cast<const char *>(pchMessage[i]), strlen(pchMessage[i]), requestData);
	}
*/

/*
    cRequestProcessor pr;

    struct timespec timeJobStart;
    struct timespec timeEnd;
    struct timespec timeDiff;

    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &timeJobStart);

    int j = 0;
    while(j < 10000000)
    {
    	pr.ProcessRequest(static_cast<const char *>(pchMessage[j % NMethods]), strlen(pchMessage[j % NMethods]), requestData);
    	j++;
    }

    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &timeEnd);
    timespec_diff(&timeJobStart, &timeEnd, &timeDiff);
    std::cout << "Time: " << timeDiff.tv_sec << " s " << timeDiff.tv_nsec << " ns. " << j << " cycles" << std::endl;
    std::cout << "Average cycle body duration: " << (timeDiff.tv_sec * 1000000000 + timeDiff.tv_nsec)/j << "ns" << std::endl;
*/
}


int main(int argc, char** argv)
{
    //testParser();
    //testServer();
    
    struct timespec timeDiff;
    
    std::map<int, struct timespec> m;
    
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &m[0]);
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &m[1]);
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &m[2]);
    
    std::cout << timespec_diff_ns(&m[0], &m[1])<< " ns" << std::endl;
    std::cout << timespec_diff_ns(&m[1], &m[2])<< " ns" << std::endl;
    
    return 0;
}
