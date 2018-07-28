#pragma once

#ifndef _WIN32
#define SOCKET int
#else
#include "winsock2.h"
#endif // !WIN_32

