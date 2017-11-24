#pragma once
#include "cPath.h"
#include "cBallGame.h"

cPath FindShortestPath(const cBallGame &game, cPath::PathItem itemStart, int xTo, int yTo);