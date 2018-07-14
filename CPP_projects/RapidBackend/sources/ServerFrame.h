
#pragma once

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