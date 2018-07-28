#pragma once

#include <string>
#include <functional>
#include <memory>

#include "DataTypes.h"


class IRequestParser
{
public:
	virtual void Parse(const std::string & request, RequestData * ) const = 0 ;
};
