// Balls.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"
#include <iostream>
#include "boost/format.hpp"
#include "Algorithms.h"

using namespace std;

int main(int argc, char *argv[])
{
	
	for (int i = 0; i < argc; i++)
	{
		cout << argv[i] << endl;
	}

	cBallGame * pGame;
	//pGame = cBallGame::GetNewInstanceFromCmdLine(argc, argv);
	pGame = cBallGame::GetInstanceFromFile("d:\\input.txt");
	if (pGame == nullptr)
	{
		cout << "Error! Cannot initialize field size. Check parameters and run program again." << endl;
		cin.get();
		return 0;
	}

	pGame->DrawTable();
	
	cout << endl << "Type ""X"" to exit or next movement. Forat ""xStart, yStart, xEnd, yEnd""" << endl;
	
	int xStart, yStart, xEnd, yEnd;
	
	char input[256];
	cin.getline(input, sizeof(input));

	while (input[0] != 'X' && input[0] != 'x') {

		string sInput(input);
		sInput.erase(std::remove(sInput.begin(), sInput.end(), ' '), sInput.end());

		int nNextStart = 0;
		int nNextEnd = sInput.find(',');
		xStart = atoi(sInput.substr(nNextStart, nNextEnd).c_str());
		
		nNextStart = nNextEnd + 1;
		nNextEnd = sInput.find(',', nNextStart);
		yStart = atoi(sInput.substr(nNextStart, nNextEnd).c_str());

		nNextStart = nNextEnd + 1;
		nNextEnd = sInput.find(',', nNextStart);
		xEnd = atoi(sInput.substr(nNextStart, nNextEnd).c_str());

		nNextStart = nNextEnd + 1;
		nNextEnd = sInput.length() - 1;
		yEnd = atoi(sInput.substr(nNextStart, nNextEnd).c_str());


		// check input
		if ((xStart < 0) || (xStart >= pGame->NColumns()) || (yStart < 0) || (yStart >= pGame->NRows()) ||
			(xEnd < 0) || (xEnd >= pGame->NColumns()) || (yEnd < 0) || (yEnd >= pGame->NRows()))
		{
			cout << "Start or finish coordinates is out of field" << endl;
		}
		else if (pGame->IsCellFree(xStart, yStart))
		{
			cout << ">>> There is no ball at [" << xStart << ":" << yStart << "]" << endl;
		}
		else if (pGame->IsCellFree(xEnd, yEnd))
		{
			cout << "Destination is occupied" << endl;
		}
		else 
		{
			// looks like input is ok
			cPath path = DijkstraFindShortestPath(*pGame, { xStart, yStart }, xEnd, yEnd);
			pGame->DrawTable(&path);
		}

		cin.getline(input, sizeof(input));
	}

	delete pGame;
	return 0;
}

