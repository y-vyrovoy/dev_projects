#pragma once
#include <list>
#include "cBallItem.h"

#define FIELD_PREFIX	"field"
#define	BALL_PREFIX		"ball"

class cBallGame
{
private:
	std::list<cBallItem> m_lstBalls;
	int m_nColumns;
	int m_nRows;

public:
	cBallGame();
	cBallGame(int nColumns, int nRows);
	~cBallGame();

	cBallGame InitislizeFromCmdLine(int argc, char *argv[]);

	bool IsCellFree(int x, int y);

	bool AddBall(int x, int y);
	void RemoveBall(int x, int y);
	void RemoveBall(cBallItem item);
	void RemoveBall(int index);
};

