#pragma once

#include <map>
#include <chrono>
#include <WinSock2.h>

#include "DataTypes.h"



class SocketContainerItem
{
public:
	SocketContainerItem(  ) : m_socket( INVALID_SOCKET ) {  }
	SocketContainerItem( SOCKET socket ) : m_socket( socket ) { tick(); }
	
	SOCKET getSocket() { return m_socket; }
	
	HiResTimePoint getLatsTick() { return m_lastTick; }

	void tick() { m_lastTick = HiResClock::now(); };

private:
	SOCKET					m_socket;
	HiResTimePoint			m_lastTick;
};

using SocketContainerType = std::map<SOCKET, SocketContainerItem>;

class SocketContainer
{
public:
	void add( SOCKET socket );
	void remove( SOCKET socket );

	void tick( SOCKET socket );

	bool contains( SOCKET socket );

	SocketContainerType::iterator begin();
	SocketContainerType::iterator end();

private:
	SocketContainerType		m_socketMap;
};
