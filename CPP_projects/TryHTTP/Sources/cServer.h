#ifndef CSERVER_H
#define	CSERVER_H

#include <vector>

#include "cSocketListener.h"
#include "cHTTPRequestParser.h"
#include "cRequestProcessor.h"
#include "cBlockingQueue.h"

class cServer
{

public:
    cServer();
    virtual ~cServer();

    int Init();
    void CloseServer();
    void StartServer();

private:

    void ListenerCallback(const REQEST_DATA & );
    void ResponserJob();
    
    cSocketListener m_sockListener;
    cHTTPRequestParser m_requestParser;
    cRequestProcessor m_requestProcessor;
    
    
    cBlockingQueue<REQEST_DATA> m_queue;
    int m_NCores;
    int m_NPullThreads;

    std::vector<std::thread> m_vecResponseThreads;
    
    std::mutex m_mtxTerminate;
    std::condition_variable m_cvTerminate;
    bool m_bForceTermination;
};

#endif	/* CSERVER_H */

