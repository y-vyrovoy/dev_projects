#ifndef SOURCES_CSOCKETLISTENER_H_
#define SOURCES_CSOCKETLISTENER_H_

#include <atomic>
#include <functional>
#include <memory>


enum class SL_INIT_RESPONCE {INIT_OK, INIT_ERR_SOCKET, INIT_ERR_SOCKOPT, INIT_ERR_BIND};

class cSocketListener {
public:
	cSocketListener();
	virtual ~cSocketListener();

	SL_INIT_RESPONCE Init();
    void StopListener();
    void StartListener(std::function<void(const char *, const int &)> requestHandler);


private:
    int m_socketListen;
    std::atomic<bool> m_bListen;

    void WaitAndHandleConnections(std::function<void(const char *, const int &)> requestHandler);
    static void HandleRequest(int sock, std::function<void(const char *, const int &)> requestHandler);
    static int SendResponce(int sock, const char * pchMessageBuffer, const int & nMessageSize, std::function<void(const char *, const int &)> requestHandler);

    void LoadDefaultResponce();
};


#endif /* SOURCES_CSOCKETLISTENER_H_ */
