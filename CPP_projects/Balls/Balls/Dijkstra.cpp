#include "stdafx.h"
#include <vector>
#include <iostream>
#include "boost/format.hpp"
#include "cPath.h"
#include "cBallGame.h"
#include "cBallItem.h"

using namespace std;

#define PRINT_MATRIX

struct dijkstraNode
{
	bool bVisited;
	cPath path;
};

unsigned int GetMatrixIndex(const cBallGame &game, int x, int y)
{
	return y * game.NColumns() + x;
}

cPath DijkstraAlgorithm(const cBallGame &game, bool ** pAdjacencyMatrix, const cBallItem & startBall, cPath::PathItem dest)
{
	int NGraphNodes = game.NColumns() * game.NRows();
	int nStartMatrixIndex = GetMatrixIndex(game, startBall.getY(), startBall.getX());

	dijkstraNode ** pPathMatrix = new dijkstraNode*[game.NColumns()];
	for (int i = 0; i < game.NColumns(); i++)
	{
		pPathMatrix[i] = new dijkstraNode[game.NRows()];
		for (int j = 0; j < game.NRows(); j++)
		{
			pPathMatrix[i][j].bVisited = false;
		}
	}

	list<cPath::PathItem> lstToCheck;
	lstToCheck.push_back({ startBall.getX(), startBall.getY() });
	pPathMatrix[startBall.getX()][startBall.getY()].path.InitStart(startBall.getX(), startBall.getY());

	// dijkstra algorithm works until there are ways to move
	while (!lstToCheck.empty())
	{
		list<cPath::PathItem> lstTemporary;

		// run along next steps list
		for (auto p = lstToCheck.begin(); p != lstToCheck.end(); ++p)
		{
			int nMatrixIndex = GetMatrixIndex(game, p->xStart, p->yStart);

			// run along current node connections (column in adjacency matrix)
			for (int j = 0; j < NGraphNodes; j++)
			{
				int xTmp = j % game.NColumns();
				int yTmp = j / game.NColumns();

				// is node connected?
				if (pAdjacencyMatrix[nMatrixIndex][j])
				{
					// if node was not visited
					if (!pPathMatrix[xTmp][yTmp].bVisited)
					{
						// if current path will be shorter, let's reassign it
						if (pPathMatrix[xTmp][yTmp].path.GetPathLength() > pPathMatrix[p->xStart][p->yStart].path.GetPathLength() + 1)
						{
							pPathMatrix[xTmp][yTmp].path = pPathMatrix[p->xStart][p->yStart].path;
							pPathMatrix[xTmp][yTmp].path.AddStep(xTmp, yTmp);
						}

						list<cPath::PathItem>::iterator it;
						for (it = lstTemporary.begin(); it != lstTemporary.end(); ++it )
						{
							if (it->xStart == xTmp && it->yStart == yTmp)
							{
								break;
							}
						}
						if (lstTemporary.empty() || it == lstTemporary.end())
						{
							lstTemporary.push_back({ xTmp, yTmp });
						}
				
						
					}

					std::cout << endl;
				}
			}

			// current node is processed
			pPathMatrix[p->xStart][p->yStart].bVisited = true;
		}

		lstToCheck = lstTemporary;
		lstTemporary.clear();

	}

	cPath pathReturn = pPathMatrix[dest.xStart][dest.yStart].path;

	for (int i = 0; i < game.NColumns(); i++)
	{
		delete[] pPathMatrix[i];
	}
	delete[] pPathMatrix;

	return pathReturn;
}

void InitMatrixDijkstra(const cBallGame &game, bool ** pDistanceMatrix, const cBallItem & startBall)
{
	int NGraphNodes = game.NColumns() * game.NRows();


	for (int iY = 0; iY < game.NRows(); iY++)
	{
		for (int iX = 0; iX < game.NColumns(); iX++)
		{
			if (!startBall.EqualCell(iX, iY) && !game.IsCellFree(iX, iY))
			{
				continue;
			}

			if ((iX + 1 < game.NColumns()) && (startBall.EqualCell(iX + 1, iY) || game.IsCellFree(iX + 1, iY)))
			{
				pDistanceMatrix[iY * game.NColumns() + iX][iY * game.NColumns() + iX + 1] = true;
				pDistanceMatrix[iY * game.NColumns() + iX + 1][iY * game.NColumns() + iX]= true;
			}

			if ((iY + 1 < game.NRows()) && (startBall.EqualCell(iX, iY + 1) || game.IsCellFree(iX, iY + 1)))
			{
				pDistanceMatrix[iY * game.NColumns() + iX][(iY + 1) * game.NColumns() + iX] = true;
				pDistanceMatrix[(iY + 1) * game.NColumns() + iX][iY * game.NColumns() + iX] = true;
			}
		}
	}
}


cPath DijkstraFindShortestPath(const cBallGame &game, cPath::PathItem itemStart, int xTo, int yTo)
{
	if ((itemStart.xStart < 0) || (itemStart.xStart >= game.NColumns()) || (itemStart.yStart < 0) || (itemStart.yStart >= game.NRows()) ||
		(xTo < 0) || (xTo >= game.NColumns()) || (yTo < 0) || (yTo >= game.NRows()))
	{
		//cout << "Start or finish coordinates is out of field" << endl;
		return cPath();
	}

	if (!game.IsCellFree(xTo, yTo))
	{
		// cout << "Destination is occupied" << endl;
		return cPath();
	}

	if (game.GetBall(itemStart.xStart, itemStart.yStart) == nullptr)
	{
		//cout << ">>> DijkstraFindShortestPath : There is no ball at [" << itemStart.xStart << ":" << itemStart.yStart << "]" << endl;
		return cPath();
	}

	// instance Graph Matrix
	int NGraphNodes = game.NColumns() * game.NRows();
	bool** pDistanceMatrix = new bool*[NGraphNodes];
	for (int i = 0; i < NGraphNodes; i++)
	{
		pDistanceMatrix[i] = new bool[NGraphNodes];
		for (int j = 0; j < NGraphNodes; j++)
		{
			pDistanceMatrix[i][j] = false;
		}
	}

	InitMatrixDijkstra(game, pDistanceMatrix, *game.GetBall(itemStart.xStart, itemStart.yStart));
	

	cPath pathReturn = DijkstraAlgorithm(game, pDistanceMatrix, *game.GetBall(itemStart.xStart, itemStart.yStart), { xTo, yTo });
	//pathReturn.print();

	for (int i = 0; i < NGraphNodes; i++)
	{
		delete[] pDistanceMatrix[i];
	}
	delete[] pDistanceMatrix;

	return pathReturn;
}
