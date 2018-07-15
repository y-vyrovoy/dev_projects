#pragma once

#include "Interfaces.h"

class RBClassFactory : public IRBFactory
{
public:
	RBClassFactory() {};
	~RBClassFactory() {};

	std::unique_ptr<IConnectionManager> createConnectionManager() override;
	std::unique_ptr<IRequestParser> createRequestParser() override;
};

class RBFakeClassFactory : public IRBFactory
{
public:
	RBFakeClassFactory() {};
	~RBFakeClassFactory() {};

	std::unique_ptr<IConnectionManager> createConnectionManager() override;
	std::unique_ptr<IRequestParser> createRequestParser() override;
};
