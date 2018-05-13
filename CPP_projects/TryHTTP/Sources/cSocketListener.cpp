
#include "cSocketListener.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <iostream>
#include <future>
#include <thread>
#include <chrono>

#define DEFAULT_HTTP_PORT 5080
#define BUF_SIZE 1024

std::string m_sAnswer;


cSocketListener::cSocketListener()
{
	m_socketListen = -1;
	m_bListen.store(false);
}

cSocketListener::~cSocketListener()
{
	StopListener();
}


SL_INIT_RESPONCE cSocketListener::Init()
{
	LoadDefaultResponce();

	int nRetVal = 0;

    m_socketListen = socket(AF_INET, SOCK_STREAM, 0);
    if (m_socketListen < 0)
    {
        std::cout << "Error: socket() failed. errno = " << errno << std::endl;
    	return SL_INIT_RESPONCE::INIT_ERR_SOCKET;
    }

    const int trueFlag = 1;
    nRetVal = setsockopt(m_socketListen, SOL_SOCKET, SO_REUSEADDR, &trueFlag, sizeof(int));
    if (nRetVal < 0)
    {
    	std::cout << "setsockopt() failed. errno: " << errno << std::endl;
    	close(m_socketListen);
    	return SL_INIT_RESPONCE::INIT_ERR_SOCKOPT;
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
    	return SL_INIT_RESPONCE::INIT_ERR_BIND;
    }

    std::cout << "bind() ok" << std::endl;

    return SL_INIT_RESPONCE::INIT_OK;
}

void cSocketListener::LoadDefaultResponce()
{
/*
	std::ifstream ifResponce;
	std::string sLine;
	try
	{
		ifResponce.open("responce.html");
		while (ifResponce >> sLine)
		{
			m_sAnswer.append(sLine);
		}

	}
	catch (const std::exception & ex)
	{
		std::cout << "Can't open responce.html" << std::endl;
	}
	ifResponce.close();
*/
}

void cSocketListener::StopListener()
{
	m_bListen.store(false);
	cSocketListener(m_socketListen);
}

void cSocketListener::StartListener(std::function<void(const char *, const int &)> requestHandler)
{
	m_bListen = true;

	WaitAndHandleConnections(requestHandler);
}

void cSocketListener::WaitAndHandleConnections(std::function<void(const char *, const int &)> requestHandler)
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
		timeout.tv_sec = 20;
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

			auto fut = std::async(std::launch::async, HandleRequest, clientSocket, requestHandler);
		}
	}
}

void cSocketListener::HandleRequest(int sock, std::function<void(const char *, const int &)> requestHandler)
{
	std::cout << "HandleRequest()" << std::endl;

	char buffer[BUF_SIZE];
	bzero(&buffer, BUF_SIZE);

	int nMessageBufferSize = BUF_SIZE;
	char * pchMessageBuffer = new char[nMessageBufferSize];

	try
	{
		int nMessageSize = 0;
		bool bProcessRequest = true;
		bool bKeepReading = true;

		int NRead;

		std::this_thread::sleep_for(std::chrono::milliseconds(50));

		while (bKeepReading)
		{
			NRead = recv(sock, &buffer, BUF_SIZE, MSG_DONTWAIT);

			if (NRead > 0)
			{
				std::cout << "NRead = " << NRead << std::endl;
			}
			else if (NRead == 0)
			{
				std::cout << "HandleRequest(). NRead == 0" << std::endl;
				bKeepReading = false;
				continue;
			}
			else if (NRead == -1)
			{
				if (errno == EAGAIN || errno == EWOULDBLOCK)
				{
					std::cout << "HandleRequest(). NRead == -1 errno == EAGAIN" << std::endl;
					bKeepReading = false;
					continue;
				}
				else
				{
					std::cout << "HandleRequest(). recv() error. errno: " << errno << std::endl;
					bProcessRequest = false;
					bKeepReading = false;
					continue;
				}
			}

			if (nMessageSize + NRead > nMessageBufferSize)
			{
				char * pchTmp = new char[nMessageBufferSize + BUF_SIZE];
				memcpy(pchTmp, pchMessageBuffer, nMessageSize);
				nMessageBufferSize += BUF_SIZE;

				std::swap(pchTmp, pchMessageBuffer);
				delete [] pchTmp;
			}

			memcpy(pchMessageBuffer + nMessageSize, buffer, NRead );
			nMessageSize += NRead;
		}

		if (!bProcessRequest)
		{
			std::cout << "HandleRequest(). recv() failed" << std::endl;
			delete [] pchMessageBuffer;
			close (sock);
			return;
		}

		if (nMessageSize <= 0)
		{
			std::cout << "HandleRequest(). The message is empty" << std::endl;
			delete [] pchMessageBuffer;
			close (sock);
			return;
		}

		std::cout << "HandleRequest(). recv() ok. NRead: " << nMessageSize << std::endl;

		pchMessageBuffer[nMessageSize] = 0;

		SendResponce(sock, pchMessageBuffer, nMessageSize, requestHandler);
		std::cout << "requestHandler() returned" << std::endl;

		close (sock);
		delete [] pchMessageBuffer;
	}
	catch (const std::exception & ex)
	{
		std::cout << "HandleRequest() raised exception. what(): " << ex.what() << std::endl;
		delete [] pchMessageBuffer;
		close (sock);
		return;
	}

}

int cSocketListener::SendResponce(int sock, const char * pchMessageBuffer, const int & nMessageSize, std::function<void(const char *, const int &)> requestHandler)
{
	requestHandler(pchMessageBuffer, nMessageSize);
	return 0;
}
