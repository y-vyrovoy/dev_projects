#include "stdafx.h"
#include <vector>
#include <iostream>
#include "boost/format.hpp"
#include "cPath.h"
#include "cBallGame.h"
#include "cBallItem.h"
#include "Algorithms.h"

using namespace std;

cPath FloydAlgorithm(const cBallGame &game, cPath ** pDistanceMatrix, const cBallItem & startBall, cPath::PathItem dest)
{
	int NGraphNodes = game.NColumns() * game.NRows();

	for (int iTmp = 0; iTmp < NGraphNodes; iTmp++)
	{
		for (int iFrom = 0; iFrom < NGraphNodes; iFrom++)
		{
			for (int iTo = 0; iTo < NGraphNodes; iTo++)
			{
				if (iFrom == iTo || iTmp == iFrom || iTmp == iTo)
				{
					continue;
				}

				if ((pDistanceMatrix[iFrom][iTmp].GetPathLength() != INF_DISTANCE) &&
					(pDistanceMatrix[iTmp][iTo].GetPathLength() != INF_DISTANCE) &&
					(pDistanceMatrix[iFrom][iTmp].GetPathLength() + pDistanceMatrix[iTmp][iTo].GetPathLength() < pDistanceMatrix[iFrom][iTo].GetPathLength()))
				{
					pDistanceMatrix[iFrom][iTo].UpdatePath(pDistanceMatrix[iFrom][iTmp], pDistanceMatrix[iTmp][iTo]);
				}
			}
		}
	}

	return pDistanceMatrix[startBall.getY() * game.NColumns() + startBall.getX()][dest.yStart * game.NColumns() + dest.xStart];
}

void InitMatrixFloyd(const cBallGame &game, cPath ** pDistanceMatrix,const cBallItem & startBall)
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

			if ( (iX + 1 < game.NColumns()) && (startBall.EqualCell(iX + 1, iY) || game.IsCellFree(iX + 1, iY)) )
			{
				pDistanceMatrix[iY * game.NColumns() + iX][iY * game.NColumns() + iX + 1].Init(iX, iY, iX + 1, iY);
				pDistanceMatrix[iY * game.NColumns() + iX + 1][iY * game.NColumns() + iX].Init(iX + 1, iY, iX, iY);
			}

			if ( (iY + 1 < game.NRows()) && (startBall.EqualCell(iX, iY + 1) || game.IsCellFree(iX, iY + 1)) )
			{
				pDistanceMatrix[iY * game.NColumns() + iX][(iY + 1) * game.NColumns() + iX].Init(iX, iY, iX, iY + 1);
				pDistanceMatrix[(iY + 1) * game.NColumns() + iX][iY * game.NColumns() + iX].Init(iX, iY + 1, iX, iY);
			}
		}
	}
}


cPath FloydFindShortestPath(const cBallGame &game, cPath::PathItem itemStart, int xTo, int yTo)
{
	if( (itemStart.xStart < 0) || (itemStart.xStart >= game.NColumns()) || (itemStart.yStart < 0) || (itemStart.yStart >= game.NRows()) ||
		(xTo < 0) || (xTo >= game.NColumns()) || (yTo < 0) || (yTo >= game.NRows()) )
	{
		return cPath();
	}

	if (!game.IsCellFree(xTo, yTo))
	{
		return cPath();
	}

	if (game.GetBall(itemStart.xStart, itemStart.yStart) == nullptr)
	{
		cout << ">>> FloydFindShortestPath : There is no ball at [" << itemStart.xStart << ":" << itemStart.yStart << "]" << endl;
		return cPath();
	}

	// instance Graph Matrix
	int NGraphNodes = game.NColumns() * game.NRows();
	cPath** pDistanceMatrix = new cPath*[NGraphNodes];
	for (int i = 0; i < NGraphNodes; i++)
	{
		pDistanceMatrix[i] = new cPath[NGraphNodes];
	}

	InitMatrixFloyd(game, pDistanceMatrix, *game.GetBall(itemStart.xStart, itemStart.yStart));
	cout << endl;

	
	
#ifdef PRINT_MATRIX	
	// print graph matrix DEBUG!!!

	cout << endl << " ----- ===== SOURCE MATRIX ==== -----" << endl;
	for (int iY = 0; iY < NGraphNodes; iY++)
	{
		cout << " " << " | ";

		for (int iX = 0; iX < NGraphNodes; iX++)
		{
			if (pDistanceMatrix[iX][iY].GetPathLength() == INF_DISTANCE)
			{
				cout << " " << " | ";
			}
			else
			{
				cout << pDistanceMatrix[iX][iY].GetPathLength() << " | ";
			}
		}
		cout << endl;
	}
	cout << endl;

	cin.get();
#endif
	
	cPath pathReturn = FloydAlgorithm(game, pDistanceMatrix, *game.GetBall(itemStart.xStart, itemStart.yStart), {xTo, yTo});

#ifdef PRINT_MATRIX
	cout << endl << " ----- ===== RESULT MATRIX ==== -----" << endl;
	for (int iY = 0; iY < NGraphNodes; iY++)
	{
		cout << " " << " | ";

		for (int iX = 0; iX < NGraphNodes; iX++)
		{

			if (pDistanceMatrix[iX][iY].GetPathLength() == INF_DISTANCE)
			{
				cout << " " << " | ";
			}
			else
			{
				cout << pDistanceMatrix[iX][iY].GetPathLength() << " | ";
			}
		}
		cout << endl;
	}
	cout << endl;
#endif

	//pathReturn.print();

	for (int i = 0; i < NGraphNodes; i++)
	{
		delete[] pDistanceMatrix[i];
	}
	delete[] pDistanceMatrix;

	return pathReturn;
}
