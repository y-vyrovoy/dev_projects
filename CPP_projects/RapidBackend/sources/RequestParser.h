#pragma once

#include <string>

#include "Interfaces.h"
#include "DataTypes.h"

class RequestParser : public IRequestParser
{
public:
	int Parse( const std::vector<char> & request, RequestData & requestDataResult ) const override;

private:
	int ParseFirstLine( const std::vector<char> & request, RequestData  & requestData ) const;
	int ParseParams( const std::vector<char> & request, size_t offset, RequestData & requestData ) const;

	HTTP_METHOD getHTTPMethod( std::vector<char>::const_iterator itBegin, std::vector<char>::const_iterator itEnd ) const;

	inline char GetDigit( char chSymbol ) const;
};