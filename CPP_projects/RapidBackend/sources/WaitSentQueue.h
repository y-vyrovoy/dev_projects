#pragma once

#include <deque>							// std::deque
#include <queue>							// std::queue
#include <memory>							// std::unique_ptr
#include <algorithm>						// std::find_if

#include "MessageException.h"

template <typename T>
class WaitSentQueue
{
public:
	WaitSentQueue() = default;

	WaitSentQueue(WaitSentQueue&&) = delete;
	WaitSentQueue(WaitSentQueue const&) = delete;

	WaitSentQueue& operator=(WaitSentQueue&&) = delete;
	WaitSentQueue& operator=(WaitSentQueue const&) = delete;

	template <typename U>
	void push( U && item );
	bool remove( T const & item );

	T& moveNextToSent();
	void moveToWaiting( T const & item );

	bool isWaitingEmpty() const; 
	bool isSentEmpty() const;
	bool isEmpty() const;


	bool isWaiting(T const& item) const; 
	bool isSent(T const& item) const;
	bool isWaitingOrSent(T const& item) const;

	

private:
	std::deque< std::unique_ptr<T> >		m_waitingRequests;
	std::deque< std::unique_ptr<T> >		m_sentRequests;

};

template <typename T>
template <typename U>
void WaitSentQueue<T>::push( U && item )
{
	m_waitingRequests.emplace_back( std::make_unique<T>( std::forward<T>( item ) ) );
}

template <typename T>
T& WaitSentQueue<T>::moveNextToSent()
{

	if ( m_waitingRequests.empty() )
	{
		THROW_MESSAGE << "Waiting queue is empty";
	}

	m_sentRequests.emplace_back( std::move( m_waitingRequests.front() ) );
	m_waitingRequests.pop_front();

	return *m_sentRequests.back();
}

template <typename T>
void WaitSentQueue<T>::moveToWaiting( T const & item )
{
	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };
	auto it = std::find_if( m_sentRequests.begin(), m_sentRequests.end(), pred );

	if ( it == m_sentRequests.end() )
	{
		THROW_MESSAGE << "Item " << item << " was not scheduled";
	}

	m_waitingRequests.emplace_back( std::move( *it ) );
	m_sentRequests.erase( it );
}

template <typename T>
bool WaitSentQueue<T>::remove( T const & item )
{
	bool bRet = false;

	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };

	auto itWait = std::find_if( m_waitingRequests.begin(), m_waitingRequests.end(), pred );
	if ( itWait != m_waitingRequests.end() )
	{
		m_waitingRequests.erase( itWait );
		bRet = true;
	}

	auto itSent = std::find_if( m_sentRequests.begin(), m_sentRequests.end(), pred );

	if ( itSent != m_sentRequests.end() )
	{
		m_sentRequests.erase( itSent );
		bRet = true;
	}

	return bRet;
}

template <typename T>
bool WaitSentQueue<T>::isWaitingEmpty() const
{
	return m_waitingRequests.empty();
}

template <typename T>
bool WaitSentQueue<T>::isSentEmpty() const
{
	return m_sentRequests.empty();
}

template <typename T>
bool WaitSentQueue<T>::isEmpty() const
{
	return m_waitingRequests.empty() || m_sentRequests.empty();
}

template <typename T>
bool  WaitSentQueue<T>::isWaiting( T const& item ) const
{
	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };

	auto itWait = std::find_if( m_waitingRequests.begin(), m_waitingRequests.end(), pred );
	if ( itWait != m_waitingRequests.end() )
	{
		return true;
	}

	return false;
}

template <typename T>
bool  WaitSentQueue<T>::isSent( T const& item ) const
{
	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };

	auto itSent = std::find_if( m_sentRequests.begin(), m_sentRequests.end(), pred );
	if ( itSent != m_sentRequests.end() )
	{
		return true;
	}

	return false;
}

template <typename T>
bool  WaitSentQueue<T>::isWaitingOrSent(T const& item) const
{
	return isWaiting( item ) || isSent( item );
}
