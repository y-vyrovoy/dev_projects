#include <iostream>
#include "cServer.h"

int main(int argc, char** argv) {

    std::cout << "Urururuuuu!" << std::endl;

    cServer serv;
    if (serv.Init() < 0)
    {
    	std::cout << "Server could not initialize" << std::endl;
    	return 0;
    }
    serv.Listen(true);

    return 0;
}

