#pragma once

#include <map>
#include <string>
#include <memory>		// std::shared_ptr


using ParameterContainer = std::map<std::string, std::string>;

class ConfigHelper
{
public:

	ConfigHelper() {};
	~ConfigHelper() {};

	virtual void parseCmdLine( const  int argn, const char * const argv[]  );

	virtual void dump() const;

	virtual const std::string get( const char * paramName ) const;

	virtual const bool getOptional( const char * paramName, std::string & paramValue ) const;

	std::string getLogFilename() const;

	std::string getRootFolder() const;

protected:

	ParameterContainer			m_params;
};

using ConfigHelperPtr = std::shared_ptr<ConfigHelper>;