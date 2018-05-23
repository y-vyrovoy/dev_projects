#ifndef SOURCES_CSOCKETLISTENER_H_
#define SOURCES_CSOCKETLISTENER_H_

#include <atomic>
#include <functional>
#include <memory>
#include <vector>

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
    int m_socketListen;
    std::atomic<bool> m_bListen;

    void WaitAndHandleConnections(SockListenerCallback requestHandler);
    
    static void HandleRequest(int sock, SockListenerCallback requestHandler);    
    static int SendResponse(const int, std::vector<char>, SockListenerCallback);
};


#endif /* SOURCES_CSOCKETLISTENER_H_ */
