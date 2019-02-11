#pragma once

#include <thread>				// std::thread
#include <atomic>				// std::atomic
#include <functional>			// std::function
#include <memory>				// std::shared_ptr


using StopFlagPtr = std::shared_ptr<std::atomic<bool>>;
using StoppableThreadJobFunc = std::function<void( StopFlagPtr )>;


class StoppableThread
{
public:
	StoppableThread() = delete;

	StoppableThread( const StoppableThreadJobFunc & job );

	StoppableThread( StoppableThread && thread );

	StoppableThread & operator=( StoppableThread && thread );

	~StoppableThread();

	void start();

	void stopAndDetach();
	void stopAndJoin();


private:
	StopFlagPtr						m_forceStop;

	StoppableThreadJobFunc			m_threadJob;

	std::thread						m_thread;
};

using StoppableThreadPtr = std::unique_ptr<StoppableThread>;