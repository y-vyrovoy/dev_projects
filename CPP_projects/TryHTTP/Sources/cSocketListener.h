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
    void StartListener(bool bNewThread);


private:
    int m_socketListen;
    std::atomic<bool> m_bListen;

    void WaitAndHandleConnections(std::function<void(const int, const char *, const int &)> requestHandler);
    static void HandleRequest(int sock, std::function<void(const int, const char *, const int &)> requestHandler);

    void LoadDefaultResponce();
};


#endif /* SOURCES_CSOCKETLISTENER_H_ */
