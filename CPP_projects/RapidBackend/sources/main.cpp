#include "stdafx.h"

#include "ServerFrame.h"

int main(int argc, char** argv)
{
    ServerFrame server;
    
    server.Initialize();
    server.StartServer();
    
    
    return 0;
}

