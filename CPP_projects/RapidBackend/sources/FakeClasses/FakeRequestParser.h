#pragma once

#include <string>

#include "../Interfaces.h"
#include "../DataTypes.h"

// ***********************************************************************
//	
//	Fake interface implementation for testing and architecture development
//
// ***********************************************************************

class FakeRequestParser : public IRequestParser
{
public:
	int Parse( const std::vector<char> & request, const RequestPtr & requestDataResult ) const override;
};
