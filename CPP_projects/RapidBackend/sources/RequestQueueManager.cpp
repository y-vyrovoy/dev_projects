#include "stdafx.h"
#include "RequestQueueManager.h"


RequestQueueManager::RequestQueueManager()
{
}


RequestQueueManager::~RequestQueueManager()
{
}

void RequestQueueManager::pushRequest(const RequestData & response)
{
	static const char * pNof = __FUNCTION__ ": ";

	m_requestQueue.push(response);
}

RequestData RequestQueueManager::pullRequest()
{
	return m_requestQueue.pull();
}

void RequestQueueManager::terminate()
{
	m_requestQueue.stop_waiting();
}