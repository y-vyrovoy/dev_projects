
#pragma once

#include <string>

#include "Interfaces.h"
#include "DataTypes.h"

class RequestParser : public IRequestParser
{
public:
    RequestParser();
    ~RequestParser();

    void Parse(const std::string & request, RequestData * requestDataResult) const;

};



// ***********************************************************************
//	
//	Fake interface implementation for testing and architecture development
//
// ***********************************************************************

class FakeRequestParser : public IRequestParser
{
public:
	FakeRequestParser() {};
	~FakeRequestParser() {};

	void Parse(const std::string & request, RequestData * requestDataResult) const;
};
