/* 
 * File:   ConnectionManager.cpp
 * Author: yura
 * 
 * Created on July 12, 2018, 9:27 AM
 */

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