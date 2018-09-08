
#pragma once

#include <memory>
#include <atomic>

#include "DataTypes.h"
#include "TCPConnectionManager.h"
#include "RequestParser.h"
#include "RequestHandler.h"
#include "ResponseDispatcher.h"

class ServerFramework
{
public:

    ServerFramework();
	~ServerFramework();


    ServerFramework( const ServerFramework& orig ) = delete;
    ServerFramework( ServerFramework&& orig ) = delete;
    
    ServerFramework & operator= ( const ServerFramework& orig ) = delete;
    ServerFramework & operator= ( ServerFramework&& orig ) = delete;
   
    
    int StartServer();
    void StopServer();
    
    void Initialize();
    void onRequest(const std::string & request);
	void onResponse(std::unique_ptr<ResponseData> response);
    
    
private:
    static std::atomic<bool> m_isServerRunning;
	std::atomic<bool> m_isInitialized;
    
    std::unique_ptr< TCPConnectionManager >	m_connectionManager;
    std::unique_ptr< RequestQueue >			m_requestQueue;
	std::unique_ptr< IRequestParser >		m_requestParser;
	std::unique_ptr< RequestHandler >		m_requestManager;
	
};

