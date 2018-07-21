#include "stdafx.h"
#include "RequestQueueManager.h"


RequestQueueManager::RequestQueueManager()
{
}


RequestQueueManager::~RequestQueueManager()
{
}

void RequestQueueManager::pushRequest(const RequestData & request)
{
	static const char * pNof = __FUNCTION__ ": ";

	m_requestQueue.push(request);
}

RequestData RequestQueueManager::pullRequest()
{
	return m_requestQueue.pull();
}

void RequestQueueManager::terminate()
{
	m_requestQueue.stop_waiting();
}