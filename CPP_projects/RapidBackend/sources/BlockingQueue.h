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
			std::unique_lock<std::mutex> lock( m_mut );
			m_deque.push_front( std::forward<T>( param ) );
		}

		m_cv.notify_all();
	}

    T pull()
    {
		std::unique_lock<std::mutex> lock( m_mut );

		m_cv.wait( lock, [this] () {return !m_deque.empty() || m_bForceStop; } );
        
        if ( m_bForceStop )
        {
			DEBUG_LOG << " Throwing cTerminationException" << std::endl;
            throw cTerminationException();
        }
            
		
		T ret{ std::move( m_deque.back() ) };
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

	void remove( T item )
	{
		std::unique_lock<std::mutex> lock(m_mut);

		auto it = std::find( m_deque.begin(), m_deque.end(), item );
		if( it != m_deque.end() )
		{
			m_deque.erase( it );
		}
		
	}

	std::string Dump()
	{
		std::stringstream ss;
		ss << "[BlockingQueue] ";

		for ( auto it = m_deque.begin(); it != m_deque.end(); it++ )
		{
			T temp = *it;
			ss << temp << " ";
		}

		return ss.str();
	}

private:
    std::mutex m_mut;
    std::deque<T> m_deque;
    std::condition_variable m_cv;
    
    std::atomic<bool> m_bForceStop;
};
