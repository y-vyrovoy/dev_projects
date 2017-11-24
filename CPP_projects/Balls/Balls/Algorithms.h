#pragma once
#include "cPath.h"
#include "cBallGame.h"

cPath FloydShortesWay(const cBallGame &game, cPath ** pDistanceMatrix, const cBallItem & startBall, cPath::PathItem dest);
cPath DijkstraShortestWay(const cBallGame &game, cPath ** pDistanceMatrix, const cBallItem & startBall, cPath::PathItem dest);