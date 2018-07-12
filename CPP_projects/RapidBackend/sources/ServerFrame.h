/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   ServerFrame.h
 * Author: yura
 *
 * Created on July 9, 2018, 11:10 AM
 */

#ifndef SERVERFRAME_H
#define SERVERFRAME_H

#include <memory>
#include <atomic>

#include "TCPConnectionManager.h"
#include "RequestParser.h"
#include "BlockingQueue.h"
#include "DataTypes.h"

class ServerFrame
{
public:

    ServerFrame();
    ServerFrame( const ServerFrame& orig ) = delete;
    ServerFrame( ServerFrame&& orig ) = delete;
    
    ServerFrame & operator= ( const ServerFrame& orig ) = delete;
    ServerFrame & operator= ( ServerFrame&& orig ) = delete;
    
    ~ServerFrame();

    
    int StartServer();
    void StopServer();
    
    void Initialize();
    void onRequest(const std::string & request);
    
    
private:
    static std::atomic<bool> m_isServerRunning;
    
    std::unique_ptr<TCPConnectionManager> m_connectionManager;
    std::unique_ptr<RequestParser> m_requestParser;
    std::unique_ptr<BlockingQueue<RequestData>> m_requestQueue;
    
};



#endif /* SERVERFRAME_H */