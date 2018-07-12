
/* 
 * File:   ServerFrame.cpp
 * Author: yura
 * 
 * Created on July 9, 2018, 11:10 AM
 */

#include "ServerFrame.h"

#include <exception>

std::atomic<bool> ServerFrame::m_isServerRunning;

ServerFrame::ServerFrame()
{
}

ServerFrame::~ServerFrame()
{
}

void ServerFrame::Initialize()
{
    m_connectionManager.reset(new TCPConnectionManager);
    m_requestParser.reset(new RequestParser);
    m_requestQueue.reset(new BlockingQueue<RequestData>);
    
    m_connectionManager->setOnRequestCallback([this](const std::string& param){onRequest(param);});
}

int ServerFrame::StartServer()
{
    if ( m_isServerRunning )
    {
        //std::exception ex("Server already runs. Only one running instance is allowed");
        throw "Server already runs. Only one running instance is allowed";//ex;
    }
}
    
void ServerFrame::StopServer()
{   
    //TODO: stop server
    m_isServerRunning = false;
}

void ServerFrame::onRequest(const std::string & request)
{
    RequestData requestData;
    m_requestParser->Parse(request, &requestData);
    
    
    // TODO: Add requestData to the queue
}