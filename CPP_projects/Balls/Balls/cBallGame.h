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

	static cBallGame * GetNewInstanceFromCmdLine(int argc, char *argv[]);

	int BallsCount() { return m_lstBalls.size();  };
	cBallItem GetBall(int index);

	bool IsCellFree(int x, int y);

	bool AddBall(int x, int y);
	void RemoveBall(int x, int y);
	void RemoveBall(cBallItem item);
	void RemoveBall(int index);

	void DrawTable();
	void print_debug();
};

