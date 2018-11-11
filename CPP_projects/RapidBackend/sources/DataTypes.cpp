#include "stdafx.h"
#include "DataTypes.h"

char strERR_METHOD[] = "ERR"; 
char strGET[] = "GET"; 
char strPUT[] = "PUT"; 
char strHEAD[] = "HEAD"; 
char strPOST[] = "POST"; 
char strTRACE[] = "TRACE"; 
char strDEL[] = "DEL"; 
char strCONNECT[] = "CONNECT"; 
char strOPTIONS[] = "OPTIONS";

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
