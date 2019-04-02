#include "stdafx.h"
#include "ConfigHelper.h"

#include <iterator>
#include <iostream>

#include "Logger.h"
#include "MessageException.h"

void ConfigHelper::parseCmdLine( const int argn, const char * const  argv[] )
{
	if ( argn < 1 )
	{
		return;
	}

	m_params["binaryName"] = argv[0];

	for ( int iParameter = 1; iParameter < argn; ++iParameter )
	{
		std::string paramName;
		size_t paramStart = 0;
		size_t paramEnd = 0;

		// parameter name should start with '-'
		if ( argv[iParameter][0] == '-' )
		{
			paramName.assign( &argv[iParameter][1] );
		}
		else
		{
			continue;
		}

		// if parameter name is last without value
		if ( iParameter + 1 >= argn )
		{
			return;
		}

		std::string paramValue;
		paramValue.assign( argv[iParameter + 1] );
			   

		ParameterContainer::iterator it = m_params.find( paramName );
		if ( it != m_params.end() )
		{
			WARN_LOG_F
				<< "Overwirting parameter [" << paramName << "]."
					" old value [" << it->second << "]"
					" new value [" << paramValue << "]";

			it->second = paramValue;
		}
		else
		{
			m_params[paramName] = paramValue;
		}

		iParameter++;
	}
}

void ConfigHelper::dump() const
{
	INFO_LOG_F << "";

	for ( std::pair<std::string, std::string> p : m_params )
	{
		INFO_LOG << '\t' << p.first << " : " << p.second;
	}
}

const std::string ConfigHelper::get( const char * paramName ) const
{
	ParameterContainer::const_iterator it = m_params.find( paramName );
	if ( it == m_params.end() )
	{
		std::stringstream ssError;
		ssError << "Cant't find config parameter [" << paramName << "]";
		throw std::runtime_error( ssError.str() );

	}

	return it->second;
}

const bool ConfigHelper::getOptional( const char * paramName, std::string & paramValue ) const
{
	try
	{
		paramValue = get( paramName );
		return true;
	}
	catch( const std::exception & )
	{
		paramValue = "";
		return false;
	}
}

const bool ConfigHelper::getOptional( const char * paramName, int & paramValue ) const
{
	try
	{
		paramValue = std::stoi( get( paramName ) );
		return true;
	}
	catch( const std::exception & )
	{
		paramValue = 0;
		return false;
	}
}

std::string ConfigHelper::getLogFilename() const
{
	std::string logFilename;
	if( getOptional("log", logFilename) )
	{
		return logFilename;
	}

	return std::string();
}

std::string ConfigHelper::getRootFolder() const
{
	std::string logFilename;
	if( getOptional("root", logFilename) )
	{
		return logFilename;
	}

	return std::string();
}