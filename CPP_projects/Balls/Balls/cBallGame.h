#pragma once
#include <list>
#include "cBallItem.h"

class cBallGame
{
private:


private:
	bool ** m_pbField;
	std::list<cBallItem> m_lstBalls;

public:
	cBallGame(int nFieldColumns, int nFieldRows);
	~cBallGame();

	bool IsCellFree(int x, int y);

	bool AddBall(int x, int y);
	void RemoveBall(int x, int y);
	void RemoveBall(cBallItem item);
	void RemoveBall(int index);


};

