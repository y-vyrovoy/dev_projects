#pragma once

#include <deque>							// std::deque
#include <queue>							// std::queue
#include <memory>							// std::unique_ptr
#include <algorithm>						// std::find_if

#include <string>							// std::string
#include <sstream>							// std::stringstream

#include "MessageException.h"

template <typename T>
class WaitSentQueue
{
public:
	using TQueue = std::deque< std::unique_ptr<T> >;

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

	size_t waitingSize() const { return m_waitingQueue.size(); };
	size_t sentSize() const { return m_sentQueue.size(); };

	bool waitingEmpty() const { return m_waitingQueue.empty(); };
	bool sentEmpty() const { return m_sentQueue.empty(); };

	auto waitingBegin() { return m_waitingQueue.begin(); };
	auto waitingEnd() { return m_waitingQueue.end(); };

	auto sentBegin() { return m_sentQueue.begin(); };
	auto sentEnd() { return m_sentQueue.end(); };
	
public:
	std::string Dump();
	

private:
	TQueue	m_waitingQueue;
	TQueue	m_sentQueue;

};

template <typename T>
template <typename U>
void WaitSentQueue<T>::push( U && item )
{
	m_waitingQueue.emplace_back( std::make_unique<T>( std::forward<T>( item ) ) );
}

template <typename T>
T& WaitSentQueue<T>::moveNextToSent()
{

	if ( m_waitingQueue.empty() )
	{
		THROW_MESSAGE << "Waiting queue is empty";
	}

	m_sentQueue.emplace_back( std::move( m_waitingQueue.front() ) );
	m_waitingQueue.pop_front();

	return *m_sentQueue.back();
}

template <typename T>
void WaitSentQueue<T>::moveToWaiting( T const & item )
{
	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };
	auto it = std::find_if( m_sentQueue.begin(), m_sentQueue.end(), pred );

	if ( it == m_sentQueue.end() )
	{
		THROW_MESSAGE << "Item " << item << " was not scheduled";
	}

	m_waitingQueue.emplace_back( std::move( *it ) );
	m_sentQueue.erase( it );
}

template <typename T>
bool WaitSentQueue<T>::remove( T const & item )
{
	bool bRet = false;

	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };

	auto itWait = std::find_if( m_waitingQueue.begin(), m_waitingQueue.end(), pred );
	if ( itWait != m_waitingQueue.end() )
	{
		m_waitingQueue.erase( itWait );
		bRet = true;
	}

	auto itSent = std::find_if( m_sentQueue.begin(), m_sentQueue.end(), pred );

	if ( itSent != m_sentQueue.end() )
	{
		m_sentQueue.erase( itSent );
		bRet = true;
	}

	return bRet;
}

template <typename T>
bool WaitSentQueue<T>::isWaitingEmpty() const
{
	return m_waitingQueue.empty();
}

template <typename T>
bool WaitSentQueue<T>::isSentEmpty() const
{
	return m_sentQueue.empty();
}

template <typename T>
bool WaitSentQueue<T>::isEmpty() const
{
	return m_waitingQueue.empty() || m_sentQueue.empty();
}

template <typename T>
bool WaitSentQueue<T>::isWaiting( T const& item ) const
{
	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };

	auto itWait = std::find_if( m_waitingQueue.begin(), m_waitingQueue.end(), pred );

	return itWait != m_waitingQueue.end();
}

template <typename T>
bool WaitSentQueue<T>::isSent( T const& item ) const
{
	auto pred = [&] (std::unique_ptr<T> const& p) { return *p == item; };

	auto itSent = std::find_if( m_sentQueue.begin(), m_sentQueue.end(), pred );

	return  itSent != m_sentQueue.end();
}

template <typename T>
bool WaitSentQueue<T>::isWaitingOrSent(T const& item) const
{
	return isWaiting( item ) || isSent( item );
}


template <typename T>
std::string WaitSentQueue<T>::Dump()
{
	std::stringstream ss;

	ss << "W:[ ";
	for( auto &item : m_waitingQueue )
	{
		ss << *item << " ";
	}
	ss << "] ";

	ss << "S:[ ";
	for( auto &item : m_sentQueue )
	{
		ss << *item << " ";
	}
	ss << "]";

	return ss.str();
}