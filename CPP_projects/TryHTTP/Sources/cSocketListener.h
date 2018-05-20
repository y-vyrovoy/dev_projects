#ifndef SOURCES_CSOCKETLISTENER_H_
#define SOURCES_CSOCKETLISTENER_H_

#include <atomic>
#include <functional>
#include <memory>
#include <vector>

enum class SL_INIT_RESPONSE {INIT_OK, INIT_ERR_SOCKET, INIT_ERR_SOCKOPT, INIT_ERR_BIND};

class cSocketListener {
public:
    cSocketListener();
    virtual ~cSocketListener();

    SL_INIT_RESPONSE Init();
    void StopListener();
    void StartListener(std::function<void(std::vector<char>, /*const char *, const int &,*/ char*, int&)> requestHandler);


private:
    int m_socketListen;
    std::atomic<bool> m_bListen;

    void WaitAndHandleConnections(std::function<void(std::vector<char>, /*const char *, const int &,*/ char*, int&)> requestHandler);
    
    static void HandleRequest(int sock, std::function<void(std::vector<char>, /*const char *, const int &,*/ char*, int&)> requestHandler);    
    static int SendResponse(const int, std::vector<char>, /*const char *, const int &,*/ std::function<void(std::vector<char>, /*const char *, const int &,*/ char*, int&)>);
};


#endif /* SOURCES_CSOCKETLISTENER_H_ */
