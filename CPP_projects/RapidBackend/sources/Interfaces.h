#pragma once

class ConnectionManager
{
public:
    ConnectionManager() {};
    ConnectionManager( const ConnectionManager & ) = delete;
    ConnectionManager( ConnectionManager && ) = delete;
    
    virtual ~ConnectionManager() {};
    
    ConnectionManager & operator= ( const ConnectionManager & ) = delete;
    ConnectionManager & operator= ( ConnectionManager && ) = delete;
    
    void setOnRequestCallback(const std::function<void(const std::string&)> &);

};
