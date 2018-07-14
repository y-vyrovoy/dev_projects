
#pragma once

#include "BlockingQueue.h"

#include <functional>

#include "DataTypes.h"
#include "Interfaces.h"

class TCPConnectionManager : public ConnectionManager
{
public:
    TCPConnectionManager();  
    ~TCPConnectionManager();
    
    void setOnRequestCallback(const std::function<void(const std::string&)> &);
    
private:
    BlockingQueue<RequestData> m_requestQueue;
    
     std::function<void(const std::string&)> m_onRequestCallback;
};

