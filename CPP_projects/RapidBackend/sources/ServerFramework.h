
#pragma once

#include <memory>
#include <atomic>

#include "DataTypes.h"
#include "TCPConnectionManager.h"
#include "RequestParser.h"
#include "RequestHandler.h"


class RequestDispatcher;

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

private:

	void onRequest( SOCKET socket, const std::vector<char> & );
	void onResponse( ResponsePtr );
	void getNextResponse( SOCKET &, ResponseData* & );
	void onResponseSent( RequestIdType id );
	
	
	static std::atomic<bool> m_isServerRunning;
	std::atomic<bool> m_isInitialized;
    

	std::unique_ptr< RequestDispatcher >		m_requestDispatcher;
    std::unique_ptr< IConnectionManager >		m_connectionManager;
    
	std::unique_ptr< IRequestParser >			m_requestParser;
	std::unique_ptr< IRequestHandler >			m_requestHandler;
};

