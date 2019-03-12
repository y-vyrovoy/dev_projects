#include "stdafx.h"
#include "CppUnitTest.h"

#include <string>
#include <vector>

#include "../sources/RequestParser.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace RapidBETests
{
	TEST_CLASS( RequestParsingTest )
	{
	public:

		TEST_METHOD( testParseGet )
		{
			char request[] =
				"GET /some/web/page?param1=12&param2=true HTTP/1.1\r"
				"Host: 127.0.0.1\r"
			;

			std::vector<char> fakeRequest;
			fakeRequest.assign( request, request + sizeof( request ) );

			RequestParser parser;
			RequestPtr result( new RequestData );

			parser.Parse( fakeRequest, result );

			Assert::IsTrue( result->http_method == HTTP_METHOD::GET );
			Assert::AreEqual( result->address, std::string( "/some/web/page?param1=12&param2=true" ) );
			Assert::AreEqual( result->nVersionMajor, 1 );
			Assert::AreEqual( result->nVersionMinor, 1 );
		}

		TEST_METHOD( testParsePost )
		{
			char request[] =
				"POST /some/web/page?param1=12&param2=true HTTP/1.1\r"
				"Host: 127.0.0.1\r"
			;

			std::vector<char> fakeRequest;
			fakeRequest.assign( request, request + sizeof( request ) );

			RequestParser parser;
			RequestPtr result( new RequestData );

			parser.Parse( fakeRequest, result );

			Assert::IsTrue( result->http_method == HTTP_METHOD::POST );
			Assert::AreEqual( result->address, std::string( "/some/web/page?param1=12&param2=true" ) );
			Assert::AreEqual( result->nVersionMajor, 1 );
			Assert::AreEqual( result->nVersionMinor, 1 );
		}

		TEST_METHOD( testParseConnect )
		{
			char request[] =
				"CONNECT /some/web/page?param1=12&param2=true HTTP/1.1\r"
				"Host: 127.0.0.1\r"
			;

			std::vector<char> fakeRequest;
			fakeRequest.assign( request, request + sizeof( request ) );

			RequestParser parser;
			RequestPtr result( new RequestData );

			parser.Parse( fakeRequest, result );

			Assert::IsTrue( result->http_method == HTTP_METHOD::CONNECT );
			Assert::AreEqual( result->address, std::string( "/some/web/page?param1=12&param2=true" ) );
			Assert::AreEqual( result->nVersionMajor, 1 );
			Assert::AreEqual( result->nVersionMinor, 1 );
		}

		TEST_METHOD( testParseParams )
		{
			char request[] =
				"GET /some/web/page?param1=12&param2=true HTTP/1.1\r"
				"Host: 127.0.0.1\r"
				"Connection: keep-alive\r"
				"Cache-Control: max-age=0\r"
				"Upgrade-Insecure-Requests: 1\r"
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36\r"
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r"
				"Accept-Encoding: gzip, deflate, br\r"
				"Accept-Language: en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6\r"
				;

			std::vector<char> fakeRequest;
			fakeRequest.assign( request, request + sizeof( request ) );

			RequestParser parser;
			RequestPtr result( new RequestData );

			parser.Parse( fakeRequest, result );

			Assert::IsTrue( result->paramsMap.find("Host") !=  result->paramsMap.end() );
			Assert::IsTrue( result->paramsMap["Host"] == "127.0.0.1" );

			Assert::IsTrue( result->paramsMap.find("Accept-Language") !=  result->paramsMap.end() );
			Assert::IsTrue( result->paramsMap["Accept-Language"] == "en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6" );

			Assert::IsTrue( result->paramsMap.find("Accept") !=  result->paramsMap.end() );
			Assert::IsTrue( result->paramsMap["Accept"] == "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8" );
		}

TEST_METHOD( testGetContentLengthNoContentLength )
		{
			char requestNoContentLength[] =
				"GET /some/web/page?param1=12&param2=true HTTP/1.1\r\n"
				"Host: 127.0.0.1\r\n"
				"Connection: keep-alive\r\n"
				"Cache-Control: max-age=0\r\n"
				"Upgrade-Insecure-Requests: 1\r\n"
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36\r\n"
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n"
				"Accept-Encoding: gzip, deflate, br\r\n"
				"Accept-Language: en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6\r\n"
				"\r\n"
			;

			std::vector<char> fakeRequestNoContenLength;
			fakeRequestNoContenLength.assign( requestNoContentLength, requestNoContentLength + sizeof( requestNoContentLength ) - 1 );

			size_t s;

			Assert::IsFalse( RequestParser::getContentLength( fakeRequestNoContenLength, s ) );
			Assert::AreEqual( s, static_cast< size_t >( 0 ) );
		}

		TEST_METHOD( testGetContentLengthWithContentLength )
		{
			char requestWithContentLength[] =
				"GET /some/web/page?param1=12&param2=true HTTP/1.1\r\n"
				"Host: 127.0.0.1\r\n"
				"Connection: keep-alive\r\n"
				"Content-Length: 10\r\n"	// !!!
				"Cache-Control: max-age=0\r\n"
				"Upgrade-Insecure-Requests: 1\r\n"
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36\r\n"
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n"
				"Accept-Encoding: gzip, deflate, br\r\n"
				"Accept-Language: en-GB,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,en-US;q=0.6\r\n"
				"\r\n"
			;

			std::vector<char> fakeRequestWithContenLength;
			fakeRequestWithContenLength.assign( requestWithContentLength, requestWithContentLength + sizeof( requestWithContentLength ) - 1 );

			size_t s;

			Assert::IsTrue( RequestParser::getContentLength( fakeRequestWithContenLength, s ) );
			Assert::AreEqual( s, static_cast< size_t >( 10 ) );

		}
	};
}