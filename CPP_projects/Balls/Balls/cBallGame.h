#pragma once
#include <list>

class cBallGame
{
private:
	struct BallItem
	{
		BallItem(int x, int y) {
			nX = x;
			nY = y;
		}

		int nX;
		int nY;
	};

private:
	bool ** m_pbField;
	std::list<BallItem> m_lstBalls;

public:
	cBallGame(int nFieldColumns, int nFieldRows);
	~cBallGame();

	bool IsCellFree(int x, int y);

	bool AddBall(int x, int y);
	void RemoveBall(int x, int y);
	void RemoveBall(BallItem item);
	void RemoveBall(int index);


};

