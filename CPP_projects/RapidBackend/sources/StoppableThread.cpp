#include "stdafx.h"
#include "StoppableThread.h"


StoppableThread::StoppableThread( const StoppableThreadJobFunc & job )
{
	m_forceStop.reset( new std::atomic<bool>( false ) );
	m_threadJob = job;
}


StoppableThread::StoppableThread( StoppableThread && thread )
{
	m_forceStop = thread.m_forceStop;
	m_thread = std::move( thread.m_thread );
}

StoppableThread & StoppableThread::operator=( StoppableThread && thread )
{
	m_forceStop = thread.m_forceStop;
	m_thread = std::move( thread.m_thread );

	return *this;
}


StoppableThread::~StoppableThread()
{
	if ( !m_forceStop )
		stopAndDetach();
}


void StoppableThread::start()
{
	*m_forceStop = false;

	std::thread tJob( [this] () { m_threadJob( m_forceStop ); } );
	m_thread = std::move( tJob );
}

void StoppableThread::stopAndDetach()
{
	*m_forceStop = true;
	m_thread.detach();
}

void StoppableThread::stopAndJoin()
{
	*m_forceStop = true;
	m_thread.join();
}