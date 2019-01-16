#pragma once

#include <vector>
#include <sstream>
#include <string>

std::vector<char> sstreamToVector( std::stringstream& src );

void saveBuffer( const char * buffer, const size_t & size, const std::string & logName );