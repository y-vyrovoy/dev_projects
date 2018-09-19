#pragma once

#include <mutex>
#include <deque>
#include <condition_variable>
#include <thread>
#include <iostream>
#include <chrono>
#include <atomic>
#include <sstream>
#include <algorithm>

#include "Logger.h"

using namespace std::chrono_literals;

class cTerminationException : public std::exception
{
};


template <typename T>
class BlockingQueue
{
public:
    
    BlockingQueue()
    {
        m_bForceStop = false;
    }

	template <typename U>
	void push(U && param)
	{
		{
			std::unique_lock<std::mutex> lock(m_mut);
			m_deque.push_front(std::forward<T>(param));
		}

		m_cv.notify_all();
	}

    T pull()
    {
        std::unique_lock<std::mutex> lock(m_mut);
        m_cv.wait(lock, [this](){return !m_deque.empty() || m_bForceStop;});
        
        if (m_bForceStop)
        {
			DEBUG_LOG << " Throwing cTerminationException" << std::endl;
            throw cTerminationException();
        }
            
        T ret(std::move(m_deque.back()));
        m_deque.pop_back();
        
        return ret;
    }
    
    void stop_waiting()
    {
        m_bForceStop = true;
        m_cv.notify_all();
    }
    
    bool is_waiting()
    {
        return !m_bForceStop;
    }
    
	size_t size()
	{
		return m_deque.size();
	}

	bool contains(const T & param) const
	{
		return !( std::find(m_deque.begin(), m_deque.end(), param) == m_deque.end() );
	}



	std::string Dump()
	{
		std::stringstream ss;
		std::for_each(m_deque.begin(), m_deque.end(), [&](const T & i) { ss << i << " "; } );
		return ss.str();
	}

private:
    std::mutex m_mut;
    std::deque<T> m_deque;
    std::condition_variable m_cv;
    
    std::atomic<bool> m_bForceStop;
};



/*
void push(const T & param)
{
	{
		std::unique_lock<std::mutex> lock(m_mut);
		m_deque.push_front(param);
	}
	m_cv.notify_all();
}

void push(T && param)
{
	{
		std::unique_lock<std::mutex> lock(m_mut);
		m_deque.push_front( std::move(param) );
	}
	m_cv.notify_all();
}

*/