#include "stdafx.h"

#include "Utils.h"

#include <fstream>
#include <iostream>

std::vector<char> sstreamToVector( std::stringstream& src )
{
	std::vector<char> dst;
	dst.reserve( static_cast< size_t >( src.str().size() ) );

	std::copy( std::istreambuf_iterator<char>( src ),
				std::istreambuf_iterator<char>(),
				std::back_inserter( dst ) );

	return dst;
}


void saveBuffer( const char * buffer, const size_t & size, const std::string & logName )
{
	std::stringstream ssFileName;
	ssFileName << "d:\\temp\\rb_logs\\" << logName << ".log";

	std::ofstream logFile( ssFileName.str(), std::ios::out | std::ios::app | std::ios::binary );

	logFile.write( buffer, size );
	logFile << "\r\n-----------------------------------\r\n";

	logFile.flush();
	logFile.close();
}
