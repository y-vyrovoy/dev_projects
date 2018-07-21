#pragma once

#include <string>
#include <functional>
#include <memory>

#include "DataTypes.h"

class IConnectionManager
{
public:
	IConnectionManager() {};
	virtual ~IConnectionManager() {};

    IConnectionManager( const IConnectionManager & ) = delete;
    IConnectionManager( IConnectionManager && ) = delete;
    IConnectionManager & operator= ( const IConnectionManager & ) = delete;
    IConnectionManager & operator= ( IConnectionManager && ) = delete;

	virtual void start() = 0;
	virtual void stop() = 0;
    
	virtual void setOnRequestCallback( const std::function<void(const std::string&)> & cb ) { m_onRequestCallback = cb; }
	virtual void sendResponse( std::unique_ptr<ResponseData> ) = 0;

protected:

	std::function<void(const std::string&)> m_onRequestCallback;
};


class IRequestParser
{
public:
	virtual void Parse(const std::string & request, RequestData * ) const = 0 ;
};

class IRBFactory
{
public:
	IRBFactory() {};
	virtual ~IRBFactory() {};

	virtual std::unique_ptr<IConnectionManager> createConnectionManager() = 0;
	virtual std::unique_ptr<IRequestParser> createRequestParser() = 0;
};