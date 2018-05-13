#ifndef CSERVER_H
#define	CSERVER_H

#include "cSocketListener.h"
#include "cRequestProcessor.h"

class cServer
{

public:
    cServer();
    virtual ~cServer();

    int Init();
    void CloseServer();
    void StartServer();

private:

    std::unique_ptr<cSocketListener> m_pListener;
    cRequestProcessor m_requestProcessor;
};

#endif	/* CSERVER_H */

