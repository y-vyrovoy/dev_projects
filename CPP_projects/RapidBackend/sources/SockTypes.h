#pragma once

#ifndef _WIN32
#define SOCKET              int
#define INVALID_SOCKET      (SOCKET)(~0)
#else
#include "winsock2.h"
#endif // !WIN_32

