#include "stdafx.h"
#include "cPath.h"
#include <iostream>

using namespace std;

unsigned int cPath::GetPathLength() 
{ 
	if (m_lstPath.size() == 0)
	{
		return INF_DISTANCE;
	}
	return m_lstPath.size() - 1; 
}

bool cPath::EqualPath(cPath::PathItem one, cPath::PathItem two)
{
	return one.xStart == two.xStart && one.yStart == two.yStart;
}

void cPath::AddStep(int xStart, int yStart, int xEnd, int yEnd)
{
	m_lstPath.push_back({ xStart, yStart });
	m_lstPath.push_back({ xEnd, yEnd });
}

void cPath::UpdatePath(cPath pathHead, cPath pathTail)
{
	m_lstPath.clear();
	m_lstPath.insert(m_lstPath.end(), pathHead.m_lstPath.begin(), pathHead.m_lstPath.end());
	m_lstPath.insert(m_lstPath.end(), next(pathTail.m_lstPath.begin(), 1), pathTail.m_lstPath.end());
}

void cPath::print()
{
	if (m_lstPath.empty())
	{
		cout << "no way" << endl;
	}

	for (auto p : m_lstPath)
	{
		cout << "[" << p.xStart << ":" << p.yStart << "]" << " -> ";
	}
	cout << endl;
}