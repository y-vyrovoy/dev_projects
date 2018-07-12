#ifndef SOURCES_CSOCKETLISTENER_H_
#define SOURCES_CSOCKETLISTENER_H_

#include <atomic>
#include <functional>
#include <memory>
#include <vector>
#include <map>
#include <thread>

#include "RequestDataTypes.h"


using SockListenerCallback = std::function<void(const REQEST_DATA & reqData)>;

class cSocketListener {
public:

    enum class enInitRet {INIT_OK, INIT_ERR_SOCKET, INIT_ERR_SOCKOPT, INIT_ERR_BIND};

    cSocketListener();
    virtual ~cSocketListener();

    enInitRet Init();
    void StopListener();
    void StartListener(SockListenerCallback requestHandler);
    int SendResponse(const REQUEST_PARAMS &, std::vector<char>);

private:
    std::thread m_ListenerThread;
    std::map<int, struct timespec> m_mapUsedSockets;
    
    int m_socketListen;
    std::atomic<bool> m_bListen;

    void WaitAndHandleConnections(SockListenerCallback requestHandler);
    
    void HandleRequest(int sock, SockListenerCallback requestHandler);    
    
    
    void TickSocket(int sock);
};


#endif /* SOURCES_CSOCKETLISTENER_H_ */
