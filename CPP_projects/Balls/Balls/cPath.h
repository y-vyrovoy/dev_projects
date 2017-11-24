#pragma once
#include <list>

#define INF_DISTANCE INT_MAX / 1 - 1

class cPath
{
public:
	struct PathItem {
		int xStart;
		int yStart;
	};

private:
	std::list<PathItem> m_lstPath;

public:

	unsigned int GetPathLength();
	void AddStep(int xStart, int yStart, int xEnd, int yEnd);
	void UpdatePath(cPath pathHead, cPath pathTail);

	bool EqualPath(PathItem one, PathItem twotem);
	void print();
};

