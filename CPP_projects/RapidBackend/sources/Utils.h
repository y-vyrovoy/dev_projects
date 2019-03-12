#pragma once

#include <vector>
#include <sstream>
#include <string>

#include <WinSock2.h>

#include "DataTypes.h"

namespace Utils
{
	std::vector<char> sstreamToVector( std::stringstream& src );

	void saveBuffer( const char * buffer, const size_t & size, const std::string & logName );

	void SaveRequest( const SOCKET socket, RequestIdType id, const std::vector<char> & request );
}


