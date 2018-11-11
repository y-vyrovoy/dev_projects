#include "stdafx.h"
#include "CppUnitTest.h"

#include <string>

#include "../sources/WaitSentQueue.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace RapidBETests
{
	TEST_CLASS( WaitingQueueTest )
	{
	public:

		TEST_METHOD( testAddMessage )
		{
			char message[] = "First message";

			WaitSentQueue<std::string> que;

			que.push( message );

			Assert::AreEqual( que.waitingSize(), static_cast< size_t >( 1 ) );
			Assert::AreEqual( que.sentSize(), static_cast< size_t >( 0 ) );
			Assert::IsTrue( que.isWaiting( message ) );
			Assert::IsFalse( que.isSent( message ) );
		}

	};
}