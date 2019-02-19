#pragma once

#include <functional>
#include <string>

#include "../Interfaces.h"

class RequestDispatcher;

class FakeRequestHandler : public IRequestHandler
{
public:
	FakeRequestHandler();
	~FakeRequestHandler();

	void Init( const ConfigHelperPtr & config,
				RequestDispatcher * requestDispatcher, 
				std::function<void( std::unique_ptr<ResponseData> )> responseCB )  override;

	void start() override;
	void stop() override;

	std::vector<char> createFaultResponse( RequestIdType id, enErrorIdType err ) const override;

protected:
	void threadJob() override;

private:
	std::vector<char>			m_standardResponse;

	std::vector<char> createResponse( const RequestData * request ) const;
	
};

