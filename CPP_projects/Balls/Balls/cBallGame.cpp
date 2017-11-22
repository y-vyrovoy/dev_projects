#include "stdafx.h"
#include "cBallGame.h"
#include "windows.h"


cBallGame::cBallGame(int nFieldColumns, int nFieldRows)
{
	m_pbField = new bool*[nFieldColumns];
	for (int i = 0; i < nFieldRows; i++) {
		m_pbField[i] = new bool[nFieldColumns];
	}
}


cBallGame::~cBallGame()
{	
	for (int i = 0; i < sizeof(m_pbField); i++) 
	{
		delete [] m_pbField[i];
	}

	delete[] m_pbField;
}

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
