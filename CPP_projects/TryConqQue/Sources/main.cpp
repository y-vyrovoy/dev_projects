#include <cstdlib>

#include <string>
#include <thread>
#include <iostream>
#include <sstream>
#include <chrono>

#include "cBlockingQueue.h"

using namespace std::chrono_literals;

static int nCounter = 0;
cBlockingQueue<std::string> q;

void job1()
{
    for (int i = 0; i < 10; i++)
    {
        std::string s;
        std::stringstream ss;
        
        ss << "message " << nCounter++;
        std::cout << "t1: pushing " << ss.str() << std::endl;
        
        q.push(ss.str());
        
        std::this_thread::sleep_for(20ms);
    }
}

void job2()
{
    while (true)
    {
        std::string s = q.pull();
        std::cout << "t2: pulling " << s << std::endl;
    }
}


int main (int argc, char** argv)
{

    std::thread t1{job1};
    std::thread t2{job2};
    
    t1.join();
    t2.join();
    
    return 0;
}

