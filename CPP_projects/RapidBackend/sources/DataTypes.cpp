#include "stdafx.h"
#include "DataTypes.h"

static const char * strERR_METHOD = "ERR"; 
static const char * strGET = "GET"; 
static const char * strPUT = "PUT"; 
static const char * strHEAD = "HEAD"; 
static const char * strPOST = "POST"; 
static const char * strTRACE = "TRACE"; 
static const char * strDEL = "DEL"; 
static const char * strCONNECT = "CONNECT"; 
static const char * strOPTIONS = "OPTIONS";

const char * getHttpMethodString( const HTTP_METHOD & method )
{
	switch( method )
	{
		case HTTP_METHOD::GET:
			return strGET;

		case HTTP_METHOD::PUT:
			return strPUT;

		case HTTP_METHOD::HEAD:
			return strHEAD;

		case HTTP_METHOD::POST:
			return strPOST;

		case HTTP_METHOD::TRACE:
			return strTRACE;

		case HTTP_METHOD::DEL:
			return strDEL;

		case HTTP_METHOD::CONNECT:
			return strCONNECT;

		case HTTP_METHOD::OPTIONS:
			return strOPTIONS;

		default:
			return strERR_METHOD;
	}
}
