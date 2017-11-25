#include "stdafx.h"
#include "cPath.h"
#include <iostream>

using namespace std;

cPath::cPath()
{
}

cPath::cPath(const cPath & path)
{
	m_lstPath.insert(m_lstPath.end(), path.m_lstPath.begin(), path.m_lstPath.end());
}

unsigned int cPath::GetPathLength() 
{ 
	if (m_lstPath.size() == 0)
	{
		return INF_DISTANCE;
	}
	return m_lstPath.size() - 1; 
}

cPath::PathItem cPath::GetStart()
{
	if (!m_lstPath.empty())
	{
		return m_lstPath.front();
	}
	return PathItem();
}

cPath::PathItem cPath::GetEnd()
{
	if (!m_lstPath.empty())
	{
		return m_lstPath.back();
	}
	return PathItem();
}

bool cPath::EqualPath(cPath::PathItem one, cPath::PathItem two)
{
	return one.xStart == two.xStart && one.yStart == two.yStart;
}

void cPath::AddStep(int x, int y)
{
	m_lstPath.push_back({ x, y });
}

void cPath::Init(int xStart, int yStart, int xEnd, int yEnd)
{
	m_lstPath.clear();
	m_lstPath.push_back({ xStart, yStart });
	m_lstPath.push_back({ xEnd, yEnd });
}

void cPath::InitStart(int xStart, int yStart)
{
	m_lstPath.clear();
	m_lstPath.push_back({xStart, yStart});
}

void cPath::UpdatePath(cPath pathHead, cPath pathTail)
{
	m_lstPath.clear();
	m_lstPath.insert(m_lstPath.end(), pathHead.m_lstPath.begin(), pathHead.m_lstPath.end());
	m_lstPath.insert(m_lstPath.end(), next(pathTail.m_lstPath.begin(), 1), pathTail.m_lstPath.end());
}

void cPath::print()
{
	if (m_lstPath.size() <= 1)
	{
		cout << "no way" << endl;
		return;
	}

	for (auto p : m_lstPath)
	{
		cout << "[" << p.xStart << ":" << p.yStart << "]" << " -> ";
	}
	cout << endl;
}

cPath & cPath::operator=(const cPath & path)
{
	m_lstPath.clear();
	m_lstPath.insert(m_lstPath.end(), path.m_lstPath.begin(), path.m_lstPath.end());
	return *this;
}