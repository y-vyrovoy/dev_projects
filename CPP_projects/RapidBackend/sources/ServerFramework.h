
#pragma once

#include <memory>
#include <atomic>

#include "DataTypes.h"
#include "TCPConnectionManager.h"
#include "RequestParser.h"
#include "RequestHandler.h"
#include "ConfigHelper.h"
	
#include "StdResponsesHelper.h"

class RequestDispatcher;


class ServerFramework
{
public:

    ServerFramework();
	~ServerFramework();


    ServerFramework( const ServerFramework& ) = delete;
    ServerFramework( ServerFramework&& ) = delete;
    
    ServerFramework & operator= ( const ServerFramework& ) = delete;
    ServerFramework & operator= ( ServerFramework&& ) = delete;
   
    
    int StartServer();
    void StopServer();
    
	void Initialize( ConfigHelperPtr & config );

private:

	void onRequest( SOCKET socket, const std::vector<char> & request );
	void onResponse( ResponsePtr response );
	void getNextResponse( SOCKET & sendSocket, ResponseData* &  response, std::chrono::milliseconds timeoutMS );
	void onResponseSent( RequestIdType id );
	
	
	static std::atomic<bool>					m_isServerRunning;
	std::atomic<bool>							m_isInitialized;
    
	std::unique_ptr< StdResponseHelper >		m_stdResponseHelper;

	std::unique_ptr< RequestDispatcher >		m_requestDispatcher;
    std::unique_ptr< IConnectionManager >		m_connectionManager;
    
	std::unique_ptr< IRequestParser >			m_requestParser;
	std::unique_ptr< IRequestHandler >			m_requestHandler;

	ConfigHelperPtr								m_config;
};

