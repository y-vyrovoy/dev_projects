#pragma once
#include <list>
#include "cBallItem.h"
#include <memory>

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

	bool IsCellFree(int x, int y) const;

	bool AddBall(int x, int y);
	const cBallItem * GetBall(int index) const;
	const cBallItem * GetBall(int x, int y)const;

	void RemoveBall(int x, int y);
	void RemoveBall(cBallItem item);
	void RemoveBall(int index);

	void DrawTable();

	int NColumns() const { return m_nColumns; } 
	int NRows() const { return m_nRows; }
};

