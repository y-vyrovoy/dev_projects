#pragma once

#include <vector>
#include <sstream>
#include <string>



#define DEFINE_PROPERTY(Type, Name)	private: Type m_##Name; \
									public:	\
										const Type & get##Name() const { return m_##Name; }  \
										void set##Name( const Type & param ) { m_##Name = param; }

std::vector<char> sstreamToVector( std::stringstream& src );

void saveBuffer( const char * buffer, const size_t & size, const std::string & logName );