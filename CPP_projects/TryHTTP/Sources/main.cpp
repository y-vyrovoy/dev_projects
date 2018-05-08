#include <iostream>
#include "cServer.h"
#include "cRequestProcessor.h"

#include <string.h>


void timespec_diff(const struct timespec *start,
					const struct timespec *stop,
					struct timespec *result)
{
    if ((stop->tv_nsec - start->tv_nsec) < 0) {
        result->tv_sec = stop->tv_sec - start->tv_sec - 1;
        result->tv_nsec = stop->tv_nsec - start->tv_nsec + 1000000000;
    } else {
        result->tv_sec = stop->tv_sec - start->tv_sec;
        result->tv_nsec = stop->tv_nsec - start->tv_nsec;
    }

    return;
}


void testServer()
{
	std::cout << "testServer()" << std::endl;

    cServer serv;
    if (serv.Init() < 0)
    {
    	std::cout << "Server could not initialize" << std::endl;
    }
    serv.StartServer();
}

void testParser()
{
	std::cout << "testParser()" << std::endl;

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
}


int main(int argc, char** argv)
{
	testServer();
	return 0;
}
