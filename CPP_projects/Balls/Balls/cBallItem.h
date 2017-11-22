#pragma once
class cBallItem
{
public:

	public:
		cBallItem();
		cBallItem(int x, int y);

		int getX() { return m_nX; }
		void setX(int x) { m_nX = x; }
		int getY() { return m_nY; }
		void setY(int y) { m_nY = y; }

		bool EqualCell(int x, int y);
		bool operator==(cBallItem);

	private:
		int m_nX;
		int m_nY;

	
};

