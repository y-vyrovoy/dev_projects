#include "stdafx.h"

#include "Utils.h"

#include <fstream>
#include <iostream>
#include <chrono>

std::vector<char> Utils::sstreamToVector( std::stringstream& src )
{
	std::vector<char> dst;
	dst.reserve( static_cast< size_t >( src.str().size() ) );

	std::copy( std::istreambuf_iterator<char>( src ),
		std::istreambuf_iterator<char>(),
		std::back_inserter( dst ) );

	return dst;
}


void Utils::saveBuffer( const char * buffer, const size_t & size, const std::string & logName )
{
	std::stringstream ssFileName;
	ssFileName << "d:\\temp\\rb_logs\\" << logName << ".log";

	std::ofstream logFile( ssFileName.str(), std::ios::out | std::ios::app | std::ios::binary );

	logFile.write( buffer, size );
	logFile << "\r\n-----------------------------------\r\n";

	logFile.flush();
	logFile.close();
}


namespace
{
	std::string GetFilename()
	{
		std::chrono::system_clock::time_point tp = std::chrono::system_clock::now();

		std::stringstream ssFileName;

		int hours = std::chrono::duration_cast< std::chrono::hours >( tp.time_since_epoch() ).count();
		int minutes = std::chrono::duration_cast< std::chrono::minutes >( tp.time_since_epoch() ).count() - hours * 60;
		long long seconds = std::chrono::duration_cast< std::chrono::seconds >( tp.time_since_epoch() ).count() - ( hours * 60 + minutes ) * 60;
		long long  ms = std::chrono::duration_cast< std::chrono::milliseconds >( tp.time_since_epoch() ).count() - ( ( hours * 60 + minutes) * 60 + seconds ) * 1000;

		ssFileName << "request_" << hours << "_" << minutes << "_" << seconds << "_" << ms << ".log";
		return ssFileName.str();
	}
};

void Utils::SaveRequest( const SOCKET socket, RequestIdType id, const std::vector<char> & request )
{
	std::stringstream ssFileName;
	ssFileName << "d:\\temp\\rb_logs\\requests\\" << GetFilename();

	std::ofstream logFile( ssFileName.str(), std::ios::out | std::ios::app | std::ios::binary );

	
	logFile.write( request.data(), request.size() );

	logFile.flush();
	logFile.close();
}