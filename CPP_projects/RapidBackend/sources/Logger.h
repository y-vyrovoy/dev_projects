#pragma once

#include <memory>			// std::unique_ptr
#include <fstream>			// std::ofstream
#include <mutex>			// std::mutex
#include <sstream>			// std::ostringstream

class Logger;
enum class enLogLevel { LOG_INFO, LOG_WARNING, LOG_ERROR, LOG_CRASH, LOG_DEBUG, LOG_SPAM, LOG_UNKNOWN };

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

	virtual void log( enLogLevel level, std::string message );

	logstream get_logstream( enLogLevel level = enLogLevel::LOG_INFO  );

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

	void log( enLogLevel level, std::string message ) override;

private:
	std::ofstream							m_oFile;
	std::unique_ptr<std::ostream>			m_stream;

	static std::unique_ptr<fileLogger>		m_instance;
};

#define INFO_LOG fileLogger::getStaticInstance().get_logstream( enLogLevel::LOG_INFO )
#define WARN_LOG fileLogger::getStaticInstance().get_logstream( enLogLevel::LOG_WARNING )
#define ERROR_LOG fileLogger::getStaticInstance().get_logstream( enLogLevel::LOG_ERROR )
#define CRASH_LOG fileLogger::getStaticInstance().get_logstream( enLogLevel::LOG_CRASH )
#define DEBUG_LOG fileLogger::getStaticInstance().get_logstream( enLogLevel::LOG_DEBUG )
#define SPAM_LOG fileLogger::getStaticInstance().get_logstream( enLogLevel::LOG_SPAM )

#define INFO_LOG_F		INFO_LOG << __FUNCTION__ << ": "
#define WARN_LOG_F		WARN_LOG << __FUNCTION__ << ": "
#define ERROR_LOG_F		ERROR_LOG << __FUNCTION__ << ": "
#define CRASH_LOG_F		CRASH_LOG << __FUNCTION__ << ": "
#define DEBUG_LOG_F		DEBUG_LOG << __FUNCTION__ << ": "
#define SPAM_LOG_F		SPAM_LOG << __FUNCTION__ << ": "

#define COUT_LOG coutLogger::getStaticInstance().get_logstream( enLogLevel::LOG_DEBUG )

#define COUT_LOG_F		COUT_LOG << __FUNCTION__ << ": "
