#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"
#include <algorithm>
#include <iostream>
#include "boost/format.hpp"
#include <iterator>

using namespace std;

cBallGame::cBallGame()
	:m_nColumns{-1}, m_nRows{-1}
{}

cBallGame::cBallGame(int nColumns, int nRows)
	:m_nColumns{nColumns}, m_nRows{nRows}
{}

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

bool cBallGame::IsCellFree(int x, int y) const {
	for (auto p = m_lstBalls.begin(); p != m_lstBalls.end(); ++p) {
		if (p->EqualCell(x, y))
		{
			return false;
		}
	}
	return true;
}

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

void cBallGame::RemoveBall(cBallItem item) 
{

}

void cBallGame::RemoveBall(int index) 
{
}

void cBallGame::DrawTable()
{
	// print header - field size + balls list
	cout << endl << " ----- ===== Actual game state ===== ----- " << endl;
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
				cout << "     |";
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


