
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


#include "LogMacro.h"
#include "RequestDataTypes.h"

#define DEFAULT_HTTP_PORT 5080
#define BUF_SIZE 1024


cSocketListener::cSocketListener()
{
    m_socketListen = -1;
    m_bListen.store(false);
}

cSocketListener::~cSocketListener()
{
    
}

cSocketListener::enInitRet cSocketListener::Init()
{
    COUT_LOG << std::endl;
    
    int nRetVal = 0;

    m_socketListen = socket(AF_INET, SOCK_STREAM, 0);
    if (m_socketListen < 0)
    {
        COUT_LOG << "Error: socket() failed. errno = " << errno << std::endl;
    	return enInitRet::INIT_ERR_SOCKET;
    }

    const int trueFlag = 1;
    nRetVal = setsockopt(m_socketListen, SOL_SOCKET, SO_REUSEADDR, &trueFlag, sizeof(int));
    if (nRetVal < 0)
    {
    	COUT_LOG << "setsockopt() failed. errno: " << errno << std::endl;
    	close(m_socketListen);
    	return enInitRet::INIT_ERR_SOCKOPT;
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
    	COUT_LOG << "bind() failed. errno: " << errno << std::endl;
    	close(m_socketListen);
    	return enInitRet::INIT_ERR_BIND;
    }

    COUT_LOG << "bind() ok" << std::endl;

    return enInitRet::INIT_OK;
}

void cSocketListener::StartListener(SockListenerCallback requestHandler)
{
    COUT_LOG << std::endl;
    m_bListen.store(true);

    auto lambda = [this, requestHandler](){WaitAndHandleConnections(requestHandler);};
    std::thread t(lambda);
    
    m_ListenerThread.swap (t);
}

void cSocketListener::StopListener()
{
    COUT_LOG << std::endl;
  
    close(m_socketListen);
    m_bListen.store(false);
    m_ListenerThread.join();
}

void cSocketListener::WaitAndHandleConnections(SockListenerCallback requestHandler)
{
    COUT_LOG << std::endl;
    
    struct sockaddr_in addrClient;
    
    while (m_bListen)
    {
        bzero((char*)&addrClient, sizeof(addrClient));
        unsigned int addrSize = sizeof(addrClient);

        int nRetVal;

        nRetVal = listen(m_socketListen, SOMAXCONN);
        if (nRetVal == -1)
        {
            COUT_LOG << "listen() returned " << nRetVal << ", errno: " << errno << std::endl;
        }
        COUT_LOG << "listen() ok" << std::endl;


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
            COUT_LOG << "select() failed. ";
            switch(nRetVal)
            {
                case EBADF:
                    COUT_LOG << "EBADF. One of the file descriptor sets specified an invalid file descriptor." << std::endl;
                    break;

                case EINTR:
                    COUT_LOG << "EINTR. The operation was interrupted by a signal" << std::endl;
                    break;

                case EINVAL:
                    COUT_LOG << "EINVAL. The timeout argument is invalid; one of the components is negative or too large." << std::endl;
                    break;
            }
            continue;
        }
        else if (nRetVal == 0)
        {
            COUT_LOG << "select() timeout" << std::endl;
            continue;
        }

        COUT_LOG << "select() got ready connection" << std::endl;

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
                        COUT_LOG << "accept() returned -1. errno = EAGAIN" << std::endl;
                        break;

                    default:
                        COUT_LOG << "accept() returned -1. errno: " << errno << std::endl;
                        break;
                }
                continue;
            }

            COUT_LOG << "accept() ok. socket: " << clientSocket << std::endl;

            auto lambda = [this, &clientSocket, &requestHandler](){HandleRequest(clientSocket, requestHandler);};
            auto fut = std::async(std::launch::async, lambda);
        }
    }
}

void cSocketListener::HandleRequest(int sock, SockListenerCallback requestHandler)
{
    COUT_LOG << std::endl;
 
    REQEST_DATA reqData;
    reqData.sock = sock;
    reqData.vecRequestBuffer.resize(BUF_SIZE);
    
    try
    {
        int nMessageSize = 0;
        unsigned long int nCurrentPosition = 0;
        bool bProcessRequest = true;
        bool bKeepReading = true;

        int NRead;

        std::this_thread::sleep_for(std::chrono::milliseconds(50));

        while (bKeepReading)
        {
            NRead = recv(sock, 
                            &(reqData.vecRequestBuffer[nCurrentPosition]), 
                            reqData.vecRequestBuffer.size() - nCurrentPosition, 
                            MSG_DONTWAIT);

            if (NRead > 0)
            {
                COUT_LOG << "NRead = " << NRead << std::endl;
            }
            else if (NRead == 0)
            {
                COUT_LOG << "NRead == 0" << std::endl;
                bKeepReading = false;
                continue;
            }
            else if (NRead == -1)
            {
                if (errno == EAGAIN || errno == EWOULDBLOCK)
                {
                    COUT_LOG << "NRead == -1 errno == EAGAIN" << std::endl;
                    bKeepReading = false;
                    continue;
                }
                else
                {
                    COUT_LOG << "recv() error. errno: " << errno << std::endl;
                    bProcessRequest = false;
                    bKeepReading = false;
                    continue;
                }
            }
            else
            {
                COUT_LOG << "The message is empty" << std::endl;
            }

            if (nCurrentPosition + NRead >= reqData.vecRequestBuffer.capacity())
            {
                reqData.vecRequestBuffer.resize(reqData.vecRequestBuffer.capacity() + BUF_SIZE);
                COUT_LOG << "buffer resize -> " << reqData.vecRequestBuffer.capacity() << std::endl;
            }

            nCurrentPosition += NRead;
            nMessageSize += NRead;
        }

        if (!bProcessRequest || nMessageSize <= 0)
        {
            close (sock);
            return;
        }

        COUT_LOG << "recv() ok. NRead: " << nMessageSize << std::endl;

        
        requestHandler(reqData);
        
        //SendResponse(sock, vecBuffer, requestHandler);
    }
    catch (const std::exception & ex)
    {
        COUT_LOG << "Exception. what(): " << ex.what() << std::endl;
        //close (sock);
        return;
    }
}

int cSocketListener::SendResponse(const REQUEST_PARAMS & reqParams, 
                                    std::vector<char> verResponce)
{
    COUT_LOG << std::endl;
    
    try
    {
        send(reqParams.sock, verResponce.data(), verResponce.size(), MSG_CONFIRM);
        close(reqParams.sock);
        //TickSocket(reqParams.sock);
    }
    catch(const std::exception & ex)
    {
       COUT_LOG << "Exception: " << ex.what() << std::endl;
    }
    
    return 0;
}

void cSocketListener::TickSocket(int sock)
{
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &m_mapUsedSockets[sock]);
}