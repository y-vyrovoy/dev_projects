#include "stdafx.h"
#include "RBClassFactory.h"

#include "TCPConnectionManager.h"
#include "RequestParser.h"



std::unique_ptr<IConnectionManager> RBClassFactory::createConnectionManager()
{
	return std::unique_ptr<IConnectionManager>(new TCPConnectionManager);
}

std::unique_ptr<IRequestParser> RBClassFactory::createRequestParser()
{
	return std::unique_ptr<IRequestParser>(new RequestParser);
}


// ***********************************************************************
//	
//	Fake interface implementation for testing and architecture development
//
// ***********************************************************************


std::unique_ptr<IConnectionManager> RBFakeClassFactory::createConnectionManager()
{
	return std::unique_ptr<IConnectionManager>(new FakeConnectionManager);
}

std::unique_ptr<IRequestParser> RBFakeClassFactory::createRequestParser()
{
	return std::unique_ptr<IRequestParser>(new FakeRequestParser);
}
