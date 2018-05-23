#ifndef CSERVER_H
#define	CSERVER_H

#include <vector>

#include "cSocketListener.h"
#include "cHTTPRequestParser.h"
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

    cSocketListener m_sockListener;
    cHTTPRequestParser m_requestParser;
    cRequestProcessor m_requestProcessor;
};

#endif	/* CSERVER_H */

