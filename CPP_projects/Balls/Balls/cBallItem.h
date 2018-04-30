#pragma once
class cBallItem
{
public:

	public:
		cBallItem();
		cBallItem(int x, int y);

		int getX() const { return m_nX; }
		void setX(int x) { m_nX = x; }
		int getY() const { return m_nY; }
		void setY(int y) { m_nY = y; }

		bool EqualCell(int x, int y) const;
		bool operator==(cBallItem);
		bool operator!=(cBallItem);

	private:
		int m_nX;
		int m_nY;
};

