#pragma once

#include <iostream>
#include <ostream>
#include <mutex>

// this is the type of std::cout
typedef std::basic_ostream<char, std::char_traits<char> > CoutType;

// this is the function signature of std::endl
typedef CoutType& (*StandardEndLine)(CoutType&);

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
	friend static Logger & operator << (Logger & log, const T & param);
	friend static Logger& operator << (Logger & log, StandardEndLine pf);

private:
	static std::mutex m_coutMutex;
};


template <typename T>
Logger & operator << ( Logger & log, const T & param) 
{
	std::lock_guard<std::mutex> lock(Logger::m_coutMutex);

	std::cout << param;
	return log;
};


Logger& operator<<(Logger & log, StandardEndLine pf)
{
	std::lock_guard<std::mutex> lock(Logger::m_coutMutex);
	
	pf(std::cout);
	return log;
}


extern Logger DebugLog;
