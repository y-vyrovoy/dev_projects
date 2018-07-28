#pragma once

#include "BlockingQueue.h"
#include "DataTypes.h"

class RequestQueueManager
{
public:
	RequestQueueManager();
	~RequestQueueManager();

	void pushRequest(const RequestData &);
	RequestData pullRequest();
	void terminate();

	size_t size() { return m_requestQueue.size(); };

private:
	BlockingQueue< std::unique_ptr<RequestData> > m_requestQueue;
};

