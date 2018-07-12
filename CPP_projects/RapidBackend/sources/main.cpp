/* 
 * File:   main.cpp
 * Author: yura
 *
 * Created on July 9, 2018, 11:06 AM
 */

#include <cstdlib>

#include "ServerFrame.h"

int main(int argc, char** argv)
{
    ServerFrame server;
    
    server.Initialize();
    server.StartServer();
    
    
    return 0;
}

