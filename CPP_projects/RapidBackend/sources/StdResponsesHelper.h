#pragma once

#include <map>
#include <vector>

enum enResponseId : unsigned int
{ 
	E200_OK = 200,

	E400_BAD_REQUEST = 400,
	E404_CANT_FIND_FILE = 404,
	E408_REQUEST_TIMEOUT = 408,

	E500_INT_SERVER_ERROR = 500,
	E520_UNKNOWN_ERROR = 520,
};

class StdResponseHelper
{
public:
	virtual void Init();
	virtual std::vector<char> createStdResponse( const enResponseId respId, const std::string & msg, bool closeSocket = false ) const;

private:
	std::map<unsigned int, std::string>		m_mapNames;
};