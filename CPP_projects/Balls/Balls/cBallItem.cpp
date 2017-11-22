#include "stdafx.h"
#include "cBallItem.h"


cBallItem::cBallItem() 
	: m_nX(-1), m_nY(-1)
{
}

cBallItem::cBallItem(int x, int y)
	: m_nX(x), m_nY(y)
{
}

bool cBallItem::EqualCell(int x, int y) 
{
	return (m_nX == x && m_nY == y);
}

bool cBallItem::operator==(cBallItem item)
{
	return EqualCell(item.m_nX, item.m_nY);
}

