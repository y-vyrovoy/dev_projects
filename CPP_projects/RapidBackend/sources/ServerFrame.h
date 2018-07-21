
#pragma once

#include <memory>
#include <atomic>

#include "TCPConnectionManager.h"
#include "RequestParser.h"
#include "RequestQueueManager.h"
#include "RequestHandler.h"
#include "DataTypes.h"

class ServerFrame
{
public:

    ServerFrame();
	~ServerFrame();


    ServerFrame( const ServerFrame& orig ) = delete;
    ServerFrame( ServerFrame&& orig ) = delete;
    
    ServerFrame & operator= ( const ServerFrame& orig ) = delete;
    ServerFrame & operator= ( ServerFrame&& orig ) = delete;
   
    
    int StartServer();
    void StopServer();
    
    void Initialize();
    void onRequest(const std::string & request);
	void onResponse(std::unique_ptr<ResponseData> response);
    
    
private:
    static std::atomic<bool> m_isServerRunning;
	std::atomic<bool> m_isInitialized;
    
    std::shared_ptr<IConnectionManager> m_connectionManager;
    std::unique_ptr<IRequestParser> m_requestParser;
    std::shared_ptr<RequestQueueManager> m_queueManager;
	std::unique_ptr<RequestHandler> m_requestManager;
};

