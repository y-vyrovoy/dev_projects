#include "stdafx.h"

#include "Utils.h"

std::vector<char> sstreamToVector( std::stringstream& src )
{
	std::vector<char> dst;
	dst.reserve( static_cast< size_t >( src.tellp() ) );
	std::copy( std::istreambuf_iterator<char>( src ),
				std::istreambuf_iterator<char>(),
				std::back_inserter( dst ) );
	return dst;
}