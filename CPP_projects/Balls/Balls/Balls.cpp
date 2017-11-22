// Balls.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"

int main()
{
	OutputDebugString(_T(">>> main()\r\n"));

	cBallGame game(8, 8);
	
	return 0;
}

