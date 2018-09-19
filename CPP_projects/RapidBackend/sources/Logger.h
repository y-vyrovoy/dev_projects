#pragma once

#include <iostream>
#include <ostream>
#include <mutex>

// this is the type of std::cout
typedef std::basic_ostream<char, std::char_traits<char> > CoutType;

// this is the function signature of std::endl
typedef CoutType& ( *StandardEndLine )( CoutType& );

class Logger
{
public:
	Logger() {};
	~Logger() {};

private:

	Logger(const Logger &) {};
	Logger(Logger &&) {};
	
	Logger & operator=(const Logger &) {};
	Logger & operator=(Logger &&) {};


public:

    template <typename T>
    Logger & operator << ( const T & param ) 
    {
        std::lock_guard<std::mutex> lock(Logger::m_coutMutex);

        std::cout << param;
        return *this;
    }


    Logger & operator << ( StandardEndLine pf )
    {
        std::lock_guard<std::mutex> lock(Logger::m_coutMutex);

        pf(std::cout);
        return *this;
    }

private:
	static std::mutex m_coutMutex;
};




extern Logger DebugLogger;
	
#define DEBUG_LOG \
	DebugLogger << __func__ << ": " 

