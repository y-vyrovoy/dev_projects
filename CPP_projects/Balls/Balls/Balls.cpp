// Balls.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"
#include <iostream>

using namespace std;


int main(int argc, char *argv[])
{
	
	for (int i = 0; i < argc; i++)
	{
		cout << argv[i] << endl;
	}

	cBallGame game;
	game.InitislizeFromCmdLine(argc, argv);

	cin.get();
	
	return 0;
}

