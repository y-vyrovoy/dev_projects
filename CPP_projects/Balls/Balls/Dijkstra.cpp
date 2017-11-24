#include "stdafx.h"
#include "Algorithms.h"
#include "cBallGame.h"

using namespace std;

cPath FloydShortesWay(const cBallGame &game, cPath ** pDistanceMatrix, const cBallItem & startBall, cPath::PathItem dest)
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

cPath DijkstraShortestWay(const cBallGame &game, cPath ** pDistanceMatrix, const cBallItem & startBall, cPath::PathItem dest)
{

}