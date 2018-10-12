#include "stdafx.h"
#include "Logger.h"

#include <iostream>
#include <fstream>

#include "MessageException.h"

#ifdef WIN32
#include <Windows.h>	
#endif

const char * PREFIX_INFO = "INFO";
const char * PREFIX_WARNING	= "WARNINIG";
const char * PREFIX_ERROR = "ERROR";
const char * PREFIX_CRASH = "CRASH";
const char * PREFIX_DEBUG = "DEBUG";
const char * PREFIX_UNKN = "UNKNOWN";

std::unique_ptr<fileLogger>		fileLogger::m_instance;
std::unique_ptr<coutLogger>		coutLogger::m_instance;

logstream::logstream( Logger & logger, enLogLevel level ) 
	: m_logger( logger )
	, m_level( level )
{
	
}

logstream::logstream( const logstream & ls )
	: m_logger( ls.m_logger )
	, m_level( ls.m_level )
{
}

logstream::~logstream()
{
	m_logger.log( m_level, str() );
}


logstream Logger::operator()( enLogLevel level )
{
	return logstream( *this, level );
}

std::string Logger::getTimeStamp()
{
#ifdef WIN32
	SYSTEMTIME time;
	GetSystemTime( &time );
	
	static char buffer[25];
	memset( &buffer, 0, 25 );
	sprintf_s( buffer, "%.2d.%.2d.%.4d %.2d:%.2d:%.2d.%.3d", 
							time.wDay, time.wMonth, time.wYear,
							time.wHour,  time.wMinute,  time.wSecond, time.wMilliseconds);

	//std::stringstream buf;
	//buf 
	//	<< time.wDay << "." << time.wMonth << "." << time.wYear
	//	<< " "
	//	<< time.wHour << ":" << time.wMinute << ":" << time.wSecond << "." << time.wMilliseconds;

	return std::string(buffer);
#endif
}

void Logger::log( enLogLevel level,  std::string message )
{
	std::unique_lock<std::mutex> lock( m_streamLock );
	const char * pPrefix;

	switch ( level )
	{
	case enLogLevel::LOG_INFO:
		pPrefix = PREFIX_INFO;
		break;

	case enLogLevel::LOG_WARNING:
		pPrefix = PREFIX_INFO;
		break;

	case enLogLevel::LOG_ERROR:
		pPrefix = PREFIX_INFO;
		break;

	case enLogLevel::LOG_CRASH:
		pPrefix = PREFIX_INFO;
		break;

	case enLogLevel::LOG_DEBUG:
		pPrefix = PREFIX_DEBUG;
		break;

	default:
		pPrefix = PREFIX_UNKN;
		break;
	}

	(*m_pStream) << getTimeStamp() << " | " << pPrefix << ": " << message << std::endl;
}




coutLogger::coutLogger()
{
	m_pStream = &std::cout;
	
}

coutLogger & coutLogger::getStaticInstance()
{
	if ( !m_instance )
	{
		m_instance.reset( new coutLogger() ); 
	}

	return *(m_instance.get()); 
};




fileLogger::fileLogger( const std::string & fileName )
{
	m_oFile.open( fileName, std::fstream::out | std::fstream::app | std::fstream::ate );
	m_pStream = &m_oFile;
}

fileLogger::~fileLogger()
{
	m_oFile.flush();
	m_oFile.close();
}

void fileLogger::initStaticInstance( const std::string & fileName ) 
{ 
	m_instance.reset( new fileLogger( fileName ) ); 
}

fileLogger & fileLogger::getStaticInstance()
{
	if ( !m_instance )
	{
		THROW_MESSAGE "fileLogger was not initialized";
	}

	return *(m_instance.get()); 
};
