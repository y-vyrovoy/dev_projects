#include "cServer.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <iostream>
#include <future>

#define DEFAULT_HTTP_PORT 5080
#define BUF_SIZE 1024


cServer::cServer()
{
	m_socketListen = -1;
	m_bListen.store(false);
}

cServer::~cServer()
{
	CloseServer();
}

int cServer::Init()
{
	int nRetVal = 0;

    m_socketListen = socket(AF_INET, SOCK_STREAM, 0);
    if (m_socketListen < 0)
    {
        std::cout << "Error: socket() failed. errno = " << errno << std::endl;
    	return -1;
    }

    const int trueFlag = 1;
    nRetVal = setsockopt(m_socketListen, SOL_SOCKET, SO_REUSEADDR, &trueFlag, sizeof(int));
    if (nRetVal < 0)
    {
    	std::cout << "setsockopt() failed. errno: " << errno << std::endl;
    	close(m_socketListen);
    	return -2;
    }

    std::cout << "socket() ok. listenSocket:" << m_socketListen << std::endl;

    struct sockaddr_in addrServer;
    bzero((char*)&addrServer, sizeof(addrServer));

    addrServer.sin_family = AF_INET;
    addrServer.sin_port = htons(DEFAULT_HTTP_PORT);
    addrServer.sin_addr.s_addr = INADDR_ANY;



    nRetVal = bind(m_socketListen, (const struct sockaddr* )&addrServer, sizeof(addrServer));
    if (nRetVal < 0)
    {
    	std::cout << "bind() failed. errno: " << errno << std::endl;
    	close(m_socketListen);
    	return -3;
    }

    std::cout << "bind() ok" << std::endl;

    return 0;
}

void cServer::CloseServer()
{
	close(m_socketListen);
}

void cServer::Listen(bool bNewThread)
{
	m_bListen = true;

	auto lambda = [](const char * pBuffer, const int & nSize)
					{
						std::cout << "we have request" << std::endl;
					};

	WaitAndHandleConnections(lambda);
}

void cServer::WaitAndHandleConnections(std::function<void(const char *, const int &)> requestHandler)
{
	struct sockaddr_in addrClient;
	while (m_bListen.load())
	{

		bzero((char*)&addrClient, sizeof(addrClient));
		unsigned int addrSize = sizeof(addrClient);


		int nRetVal;

		nRetVal = listen(m_socketListen, SOMAXCONN);
		if (nRetVal == -1)
		{
			std::cout << "listen() returned " << nRetVal << ", errno: " << errno << std::endl;
		}
		std::cout << "listen() ok" << std::endl;


		struct timeval timeout;

		/* Initialize the timeout data structure. */
		timeout.tv_sec = 5;
		timeout.tv_usec = 0;

		fd_set set;


		/* Initialize the file descriptor set. */
		FD_ZERO (&set);
		FD_SET (m_socketListen, &set);

		nRetVal = select (FD_SETSIZE, &set, NULL, NULL, &timeout);

		if (nRetVal == -1)
		{
			std::cout << "select() failed. ";
			switch(nRetVal)
			{
				case EBADF:
					std::cout << "EBADF. One of the file descriptor sets specified an invalid file descriptor." << std::endl;
					break;

				case EINTR:
					std::cout << "EINTR. The operation was interrupted by a signal" << std::endl;
					break;

				case EINVAL:
					std::cout << "EINVAL. The timeout argument is invalid; one of the components is negative or too large." << std::endl;
					break;
			}
		  continue;
		}
		else if (nRetVal == 0)
		{
		  std::cout << "select() timeout" << std::endl;
		  continue;
		}

		std::cout << "select() got ready connection" << std::endl;

		if (FD_ISSET(m_socketListen, &set))
		{
			int clientSocket = accept(m_socketListen,
										(struct sockaddr*)&addrClient,
										&addrSize);
			if (clientSocket < 0)
			{
				switch (errno)
				{
					case EAGAIN:
						std::cout << "accept() returned -1. errno = EAGAIN" << std::endl;
						break;

					default:
						std::cout << "accept() returned -1. errno: " << errno << std::endl;
						break;
				}
				continue;
			}

			std::cout << "accept() ok. socket: " << clientSocket << std::endl;
			HandleRequest(clientSocket, requestHandler);
		}
	}
}

void cServer::HandleRequest(int sock, std::function<void(const char *, const int &)> requestHandler)
{
	std::cout << "requestHandler()" << std::endl;

	int NRead;
	char buffer[BUF_SIZE];
	bzero(&buffer, BUF_SIZE);

	try
	{
		NRead = read(sock, &buffer, BUF_SIZE);
	}
	catch (std::exception ex)
	{
		std::cout << "read() raised exception. what(): " << ex.what() << std::endl;
		return;
	}

	if (NRead == -1)
	{
		std::cout << "read() failed. errno: " << errno << std::endl;
		return;
	}

	std::cout << "read() ok. NRead: " << NRead << std::endl;

	requestHandler(buffer, NRead);
	//auto f = std::async(std::launch::async, requestHandler, buffer, NRead);
	std::cout << "requestHandler() returned" << std::endl;
}
