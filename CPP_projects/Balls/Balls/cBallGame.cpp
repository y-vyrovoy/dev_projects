#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"
#include <algorithm>
#include <iostream>
#include "boost/format.hpp"
#include <iterator>
#include <fstream>


using namespace std;

cBallGame::cBallGame()
	:m_nColumns{-1}, m_nRows{-1}
{}

cBallGame::cBallGame(int nColumns, int nRows)
	:m_nColumns{nColumns}, m_nRows{nRows}
{}

// istantiates cBallGame reading file
cBallGame * cBallGame::GetInstanceFromFile(string sFileName)
{
	vector<string> lstReturn;

	ifstream file;
	try {
		file.open(sFileName);
		string str;
		while (getline(file, str))
		{
			lstReturn.push_back(str);
		}
		file.close();
	}
	catch (ifstream::failure e)
	{
		std::cout << "Cannot read parameters file" << endl;
		file.close();
		return nullptr;
	}

	int i = 0;
	char **argv = new char*[lstReturn.size()];
	for (auto p : lstReturn)
	{
		argv[i] = new char[p.length() + 1];
		strcpy_s(argv[i], p.length()+1, p.c_str());
		i++;
	}
	return GetNewInstanceFromCmdLine(lstReturn.size(), argv);
}

// istantiates cBallGame from string array (command line)
cBallGame * cBallGame::GetNewInstanceFromCmdLine(int argc, char *argv[])
{
	cBallGame * pReturn = nullptr;
	bool bFieldSetuped = false;

	for (int i = 0; i < argc; i++)
	{
		string sNextCmd(argv[i]);

		int nPrefixEnd = sNextCmd.find("[");
		if (nPrefixEnd == string::npos) {
			continue;
		}
		string sPrefix = sNextCmd.substr(0, nPrefixEnd);

		if (sPrefix == FIELD_PREFIX)
		{
			bFieldSetuped = true;
			try {
				sNextCmd.erase(std::remove(sNextCmd.begin(), sNextCmd.end(), ' '), sNextCmd.end());
				int nStart = sNextCmd.find("[") + 1;
				int nEnd = sNextCmd.find("]") - 1;
				int nComma = sNextCmd.find(",");

				string sColumns = sNextCmd.substr(nStart, nComma - nStart );
				int NColumns = atoi(sColumns.c_str());

				string sRows = sNextCmd.substr(nComma + 1, nEnd - nComma);
				int NRows = atoi(sRows.c_str());

				if (NColumns > 0 && NRows > 0) {
					pReturn = new cBallGame(NColumns, NRows);
				}
				else
				{
					return pReturn;
				}
			}
			catch (...) {
				delete pReturn;
				return nullptr;
			}
		}
		else if (sPrefix == BALL_PREFIX)
		{
			if (i > 0 && !bFieldSetuped) {
				delete pReturn;
				return nullptr;
			}

			try {
				sNextCmd.erase(std::remove(sNextCmd.begin(), sNextCmd.end(), ' '), sNextCmd.end());
				int nStart = sNextCmd.find("[") + 1;
				int nEnd = sNextCmd.find("]") - 1;
				int nComma = sNextCmd.find(",");

				string sX = sNextCmd.substr(nStart, nComma - nStart);
				int nX = atoi(sX.c_str());

				string sY = sNextCmd.substr(nComma + 1, nEnd - nComma);
				int nY = atoi(sY.c_str());

				if (nX >= 0 && nY >= 0) {
					pReturn->AddBall(nX, nY);
				}
			}
			catch (...) {}
		}		
	}

	return pReturn;
}

cBallGame::~cBallGame()
{}

// checks is cell free or not
bool cBallGame::IsCellFree(int x, int y) const {
	for (auto p = m_lstBalls.begin(); p != m_lstBalls.end(); ++p) {
		if (p->EqualCell(x, y))
		{
			return false;
		}
	}
	return true;
}

// add new ball to game
bool cBallGame::AddBall(int x, int y) 
{
	if (x >= m_nColumns || y >= m_nRows)
	{
		return false;
	}

	if (IsCellFree(x, y)) {
		cBallItem pItem(x, y);
		m_lstBalls.push_back(pItem);
		return true;
	}
	else {
		return false;
	}
}

const cBallItem * cBallGame::GetBall(int index) const
{
	std::list<cBallItem>::const_iterator pItem = m_lstBalls.begin();
	for (int i = 0; i < m_lstBalls.size(); i++)
	{
		if (i == index)
		{
			return &(*pItem);
		}
		pItem++;
	}
	return nullptr;
}

const cBallItem * cBallGame::GetBall(int x, int y) const
{
	std::list<cBallItem>::const_iterator pItem = m_lstBalls.begin();
	for (int i = 0; i < m_lstBalls.size(); i++)
	{
		if (pItem->EqualCell(x, y) )
		{
			return &(*pItem);
		}
		pItem++;
	}
	return nullptr;
}

void cBallGame::RemoveBall(int x, int y) 
{
	for (auto p = m_lstBalls.begin(); p != m_lstBalls.end(); ++p) {
		if (p->EqualCell(x, y))
		{
			m_lstBalls.remove(*p);
		}
	}
}


// prints field, balls and path
void cBallGame::DrawTable(cPath * pPath)
{
	cout << endl;

	// print header - field size + balls list
	if (pPath == nullptr)
	{
		cout << endl << " ----- ===== Actual game state ===== ----- " << endl;
	}
	else
	{
		cout << endl << " ----- ===== Path from [" 
					<< pPath->GetStart().xStart << ":" 
					<< pPath->GetStart().yStart << "] -> [" 
					<< pPath->GetStart().xStart << ":"
					<< pPath->GetStart().yStart << "] "
					<<" ===== ----- " << endl;
	}

	cout << endl << "Fields: cols=" << m_nColumns << ", rows=" << m_nRows ;
	cout << endl << "Balls: ";

	for (auto p : m_lstBalls)
	{
		cout << "[" << p.getX() << ", " << p.getY() << "]";
		if (p != m_lstBalls.back()) {
			cout << ", ";
		}
		else {
			cout << endl;
		}
	}
	cout << endl;


	// print the table
	
	// top border
	char horizontalDash[] = "-----|";

	cout << horizontalDash;
	for (int i = 0; i < m_nColumns; ++i)
	{
		cout << horizontalDash;
	}
	cout << endl;

	// table body
	for (int iY = m_nRows - 1; iY >= 0; iY--)
	{
		cout << boost::format("y: %02i") % (iY % 100) << "|";

		// draw cells
		for (int iX = 0; iX < m_nColumns; ++iX)
		{
			if (IsCellFree(iX, iY))
			{
				if (pPath == nullptr)
				{
					cout << "     |";
				}
				else
				{
					cout << "  " << pPath->GetNextStep(iX, iY) << "  |";
				}
			} 
			else
			{
				cout << "  o  |";
			}
		}
		cout << endl;
		
		
		cout << horizontalDash;
		// draw bottom line
		for (int iX = 0; iX < m_nColumns; ++iX)
		{
			cout << horizontalDash;
		}
		cout << endl;
	}

	// draw X coordinates
	cout << "     |";
	for (int iX = 0; iX < m_nColumns; ++iX)
	{
		cout << boost::format("x: %02i") % (iX % 100) << "|";
	}
	cout << endl;


}


