#pragma once

#include <memory>			// std::unique_ptr
#include <sstream>
#include <fstream>			// std::ofstream
#include <mutex>			// std::mutex

class Logger;
enum class enLogLevel { LOG_INFO, LOG_WARNING, LOG_ERROR, LOG_CRASH, LOG_DEBUG, LOG_UNKNOWN };

class logstream : public std::ostringstream
{
public:
	logstream( Logger & logger, enLogLevel level );
	logstream( const logstream & ls );
	~logstream();

private:
	Logger&					m_logger;
	enLogLevel				m_level;
};

class Logger
{
public:

	logstream operator()( enLogLevel level = enLogLevel::LOG_INFO );
	void log( enLogLevel level, std::string message );

private:	
	static std::string getTimeStamp();
	
protected:
	std::ostream * m_pStream;
	
private: 
	std::mutex m_streamLock;

};

class coutLogger : public Logger
{
public:
	coutLogger();

	static coutLogger & getStaticInstance();

private:
	static std::unique_ptr<coutLogger>		m_instance;
};

class fileLogger : public Logger
{
public:
	fileLogger( const std::string & fileName );
	~fileLogger();

	static void initStaticInstance( const std::string & fileName );
	static fileLogger & getStaticInstance();

private:
	std::ofstream m_oFile;
	std::unique_ptr<std::ostream> m_stream;

	static std::unique_ptr<fileLogger>		m_instance;
};

#define INFO_LOG fileLogger::getStaticInstance().operator()(enLogLevel::LOG_INFO)
#define WARNING_LOG fileLogger::getStaticInstance().operator()(enLogLevel::LOG_WARNING)
#define ERROR_LOG fileLogger::getStaticInstance().operator()(enLogLevel::LOG_ERROR)
#define CRASH_LOG fileLogger::getStaticInstance().operator()(enLogLevel::LOG_CRASH)
#define DEBUG_LOG fileLogger::getStaticInstance().operator()(enLogLevel::LOG_DEBUG)


#define COUT_LOG coutLogger::getStaticInstance().operator()(enLogLevel::LOG_DEBUG)
