// Balls.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"
#include <iostream>
#include "boost/format.hpp"

using namespace std;

int main(int argc, char *argv[])
{
	
	for (int i = 0; i < argc; i++)
	{
		cout << argv[i] << endl;
	}

	cBallGame * pGame;
	pGame = cBallGame::GetNewInstanceFromCmdLine(argc, argv);
	if (pGame == nullptr)
	{
		cout << "Error! Cannot initialize field size. Check parameters and run program again." << endl;
		cin.get();
		return 0;
	}

	pGame->DrawTable();
	cin.get();
	
	delete pGame;
	return 0;
}

