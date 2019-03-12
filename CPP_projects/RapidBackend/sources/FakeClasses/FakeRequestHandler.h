#pragma once

#include <functional>
#include <string>

#include "../Interfaces.h"

class RequestDispatcher;
class StdResponseHelper;

class FakeRequestHandler : public IRequestHandler
{
public:
	FakeRequestHandler();
	~FakeRequestHandler();

	void Init( const ConfigHelperPtr & config,
				StdResponseHelper * stdResponseHelper, 
				RequestDispatcher * requestDispatcher, 
				std::function<void( std::unique_ptr<ResponseData> )> responseCB )  override;

	void start() override;
	void stop() override;

protected:
	void threadJob() override;

private:
	std::vector<char>			m_standardResponse;

	std::vector<char> createResponse( const RequestData * request ) const;
	
};

