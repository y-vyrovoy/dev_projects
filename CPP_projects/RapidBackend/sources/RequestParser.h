#pragma once

#include <string>

#include "Interfaces.h"
#include "DataTypes.h"

class RequestParser : public IRequestParser
{
public:
	int Parse( const std::vector<char> & request, RequestData & requestDataResult ) const override;

	static bool getHeaderLength( std::vector<char> & vecBuffer, size_t & outLength );
	static bool getContentLength( std::vector<char> & vecBuffer, size_t & outLength );
	static bool isHeaderValid( std::vector<char> & vecBuffer );
	static HTTP_METHOD getHTTPMethod( std::vector<char>::const_iterator itBegin, std::vector<char>::const_iterator itEnd );

private:
	int ParseStartLine( const std::vector<char> & request, RequestData  & requestData ) const;
	int ParseParams( const std::vector<char> & request, size_t offset, RequestData & requestData ) const;

	

	inline char GetDigit( char chSymbol ) const;
};