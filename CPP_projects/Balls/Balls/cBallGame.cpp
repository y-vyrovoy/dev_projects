#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"
#include <algorithm>

using namespace std;

cBallGame::cBallGame()
	:m_nColumns{-1}, m_nRows{-1}
{}

cBallGame::cBallGame(int nColumns, int nRows)
	:m_nColumns{nColumns}, m_nRows{nRows}
{}

cBallGame  cBallGame::InitislizeFromCmdLine(int argc, char *argv[])
{
	cBallGame gameNew;

	for (int i = 0; i < argc; i++)
	{
		string sNext(argv[i]);
		int nPrefixPos = sNext.find(FIELD_PREFIX, 0);

		if (nPrefixPos != string::npos)
		{
			try {
				sNext.erase(std::remove(sNext.begin(), sNext.end(), ' '), sNext.end());
				int nStart = sNext.find("[") + 1;
				int nEnd = sNext.find("]") - 1;
				int nComma = sNext.find(",");

				string sColumns = sNext.substr(nStart, nComma - nStart );
				gameNew.m_nColumns = atoi(sColumns.c_str());

				string sRows = sNext.substr(nComma + 1, nEnd - nComma);
				gameNew.m_nRows = atoi(sRows.c_str());
			}
			catch (...) {
				gameNew.m_nColumns = -1;
				gameNew.m_nRows = -1;
				return gameNew;
			}
		}

		
	}

	return gameNew;
}



cBallGame::~cBallGame()
{}

bool cBallGame::IsCellFree(int x, int y) {
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
	if (IsCellFree(x, y)) {
		cBallItem pItem(x, y);
		m_lstBalls.push_back(pItem);
		return true;
	}
	else {
		return false;
	}
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
