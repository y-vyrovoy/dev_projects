#pragma once

#include <functional>
#include <string>

#include "../BaseRequestHandler.h"

class RequestDispatcher;

class FakeRequestHandler : public BaseRequestHandler
{
public:
	FakeRequestHandler();
	~FakeRequestHandler();

	void Init( const ConfigHelperPtr & config,
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

