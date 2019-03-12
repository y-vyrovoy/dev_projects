#pragma once

#include <string>

#include "Interfaces.h"
#include "DataTypes.h"

class RequestParser : public IRequestParser
{
public:
	int Parse( const std::vector<char> & request, const RequestPtr & requestDataResult ) const override;

	static bool getHeaderLength( std::vector<char> & vecBuffer, size_t & outLength );
	static bool getContentLength( std::vector<char> & vecBuffer, size_t & outLength );
	static bool isHeaderValid( std::vector<char> & vecBuffer );
	static HTTP_METHOD charToHttpMethod( std::vector<char>::const_iterator itBegin, std::vector<char>::const_iterator itEnd );

private:
	static HTTP_METHOD parseHttpMethod( const std::vector<char> & vecBuffer);
	static std::string parseHeaderParams( const std::vector<char> & vecBuffer );
	static std::pair<char, char> parseHttpVersion( const std::vector<char> & vecBuffer );

	int ParseStartLine( const std::vector<char> & request, const RequestPtr & requestData ) const;
	int ParseParams( const std::vector<char> & request, const RequestPtr & requestData ) const;

	

	inline char GetDigit( char chSymbol ) const;
};