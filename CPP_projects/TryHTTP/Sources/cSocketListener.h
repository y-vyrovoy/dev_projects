#ifndef SOURCES_CSOCKETLISTENER_H_
#define SOURCES_CSOCKETLISTENER_H_

#include <atomic>
#include <functional>
#include <memory>
#include <vector>
#include <map>
#include <thread>

enum class SL_INIT_RESPONSE {INIT_OK, INIT_ERR_SOCKET, INIT_ERR_SOCKOPT, INIT_ERR_BIND};
using SockListenerCallback = std::function<std::vector<char>(const std::vector<char>&)>;

class cSocketListener {
public:
    cSocketListener();
    virtual ~cSocketListener();

    SL_INIT_RESPONSE Init();
    void StopListener();
    void StartListener(SockListenerCallback requestHandler);


private:
    std::thread m_ListenerThread;
    std::map<int, struct timespec> m_mapUsedSockets;
    
    int m_socketListen;
    std::atomic<bool> m_bListen;

    void WaitAndHandleConnections(SockListenerCallback requestHandler);
    
    void HandleRequest(int sock, SockListenerCallback requestHandler);    
    int SendResponse(const int, std::vector<char>, SockListenerCallback);
    
    void TickSocket(int sock);
};


#endif /* SOURCES_CSOCKETLISTENER_H_ */
