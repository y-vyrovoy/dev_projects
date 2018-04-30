#pragma once
#include "cPath.h"
#include "cBallGame.h"

cPath FloydFindShortestPath(const cBallGame &game, cPath::PathItem itemStart, int xTo, int yTo);
cPath DijkstraFindShortestPath(const cBallGame &game, cPath::PathItem itemStart, int xTo, int yTo);