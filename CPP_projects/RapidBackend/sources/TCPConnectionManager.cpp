#include "stdafx.h"

#include "TCPConnectionManager.h"

TCPConnectionManager::TCPConnectionManager()
{
}

TCPConnectionManager::~TCPConnectionManager()
{
}

void TCPConnectionManager::setOnRequestCallback(const std::function<void(const std::string&)> & cb)
{
    m_onRequestCallback = cb;
}