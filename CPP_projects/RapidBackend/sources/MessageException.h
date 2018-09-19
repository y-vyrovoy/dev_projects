#pragma once

#include <string>
#include <sstream>


class MessageException : public std::exception
{
public:
	MessageException() noexcept {};
	MessageException( const MessageException & ex ) noexcept : m_message( ex.m_message ) {};
	MessageException( MessageException && ex ) noexcept : m_message( std::move(ex.m_message) ) {};
	MessageException( const std::string & message ) noexcept : m_message( message ) {};
	MessageException( std::string && message ) noexcept : m_message( std::move(message) ) {};

	MessageException& operator= ( const MessageException & ex ) noexcept { m_message = ex.m_message; };
	MessageException& operator= ( MessageException && ex ) noexcept { m_message = std::move(ex.m_message); };

	virtual const char * what() const noexcept { return m_message.c_str(); };

	template <typename T>
	friend MessageException& operator<< ( MessageException&  ex, T message );

	static MessageException getInstance() { return MessageException(); };

private:
	std::string m_message;
};


template <typename T>
MessageException& operator<< ( MessageException& ex, T message )
{
	std::stringstream ss;
	ss << message;
	ex.m_message.append( ss.str() );
	return ex;
}

#define THROW_MESSAGE \
	MessageException ex; \
	throw ex << __func__ << ": "