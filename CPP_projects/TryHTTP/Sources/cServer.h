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

#include "cSocketListener.h"

class cServer
{

public:
    cServer();
    virtual ~cServer();

    int Init();
    void CloseServer();
    void StartServer();

private:

    cSocketListener m_Listener;

};

#endif	/* CSERVER_H */

