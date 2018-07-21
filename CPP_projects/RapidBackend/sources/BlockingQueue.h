#pragma once

#include <mutex>
#include <deque>
#include <condition_variable>
#include <thread>
#include <iostream>
#include <chrono>
#include <atomic>

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
    
    void push(const T & param)
    {
        {
            std::unique_lock<std::mutex> lock(m_mut);
            m_deque.push_front(param);
        }
        
        m_cv.notify_all();
    }
    
    T pull()
    {
        std::unique_lock<std::mutex> lock(m_mut);
        m_cv.wait(lock, [=](){return !this->m_deque.empty() || m_bForceStop;});
        
        if (m_bForceStop)
        {
			DebugLog << __func__ << " Throwing cTerminationException" << std::endl;
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

private:
    std::mutex m_mut;
    std::deque<T> m_deque;
    std::condition_variable m_cv;
    
    std::atomic<bool> m_bForceStop;
        
};
