/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   cServer.h
 * Author: yura
 *
 * Created on April 24, 2018, 12:00 PM
 */

#ifndef CSERVER_H
#define	CSERVER_H

#include <atomic>
#include <functional>

class cServer
{

public:
    cServer();
    virtual ~cServer();

    int Init();
    void CloseServer();
    void Listen(bool bNewThread);

private:
    int m_socketListen;
    std::atomic<bool> m_bListen;

    void WaitAndHandleConnections(std::function<void(const char *, const int &)> requestHandler);
    void HandleRequest(int sock, std::function<void(const char *, const int &)> requestHandler);
};

#endif	/* CSERVER_H */

