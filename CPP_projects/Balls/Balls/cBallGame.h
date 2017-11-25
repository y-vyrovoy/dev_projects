#pragma once
#include <list>
#include "cBallItem.h"
#include <memory>
#include "cPath.h"

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
	static cBallGame * GetInstanceFromFile(std::string sFileName);

	int BallsCount() { return m_lstBalls.size();  };

	bool IsCellFree(int x, int y) const;

	bool AddBall(int x, int y);
	const cBallItem * GetBall(int index) const;
	const cBallItem * GetBall(int x, int y)const;

	void RemoveBall(int x, int y);

	void DrawTable(cPath * pPath = nullptr);

	int NColumns() const { return m_nColumns; } 
	int NRows() const { return m_nRows; }



};

