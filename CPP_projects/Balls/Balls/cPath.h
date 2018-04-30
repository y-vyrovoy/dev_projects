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

	cPath();
	cPath(const cPath & path);

	unsigned int GetPathLength();
	void AddStep(int x, int y);
	void InitStart(int xStart, int yStart);
	void Init(int xStart, int yStart, int xEnd, int yEnd);
	void UpdatePath(cPath pathHead, cPath pathTail);

	PathItem GetStart();
	PathItem GetEnd();

	bool EqualPath(PathItem one, PathItem twotem);
	void print();

	cPath & operator=(const cPath & path);
	std::string GetNextStep(int x, int y);
};

