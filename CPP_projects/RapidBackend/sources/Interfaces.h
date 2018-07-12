/* 
 * File:   Interfaces.h
 * Author: yura
 *
 * Created on July 12, 2018, 7:51 PM
 */

#ifndef INTERFACES_H
#define INTERFACES_H


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

#endif /* INTERFACES_H */

