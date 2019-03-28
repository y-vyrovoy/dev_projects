#include "stdafx.h"
#include "StoppableThread.h"
#include "SocketContainer.h"

void SocketContainer::add( SOCKET socket )
{
	m_socketMap[socket] = SocketContainerItem( socket );
}

void SocketContainer::remove( SOCKET socket )
{
	auto it = m_socketMap.find( socket );
	if ( it != m_socketMap.end() )
		m_socketMap.erase( socket );
}

void SocketContainer::tick( SOCKET socket )
{
	auto it = m_socketMap.find( socket );
	if ( it != m_socketMap.end() )
		it->second.tick();
}

bool SocketContainer::contains( SOCKET socket )
{
	return ( m_socketMap.find( socket ) != m_socketMap.end() );
}

SocketContainerType::iterator SocketContainer::begin()
{
	return m_socketMap.begin();
}

SocketContainerType::iterator SocketContainer::end()
{
	return m_socketMap.end();
}
