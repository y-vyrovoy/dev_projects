#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux
CND_DLIB_EXT=so
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/sources/Logger.o \
	${OBJECTDIR}/sources/MessageException.o \
	${OBJECTDIR}/sources/RequestHandler.o \
	${OBJECTDIR}/sources/RequestParser.o \
	${OBJECTDIR}/sources/ResponseDispatcher.o \
	${OBJECTDIR}/sources/ServerFramework.o \
	${OBJECTDIR}/sources/TCPConnectionManager.o \
	${OBJECTDIR}/sources/main.o \
	${OBJECTDIR}/sources/stdafx.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rapidbackend

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rapidbackend: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rapidbackend ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/sources/Logger.o: sources/Logger.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/Logger.o sources/Logger.cpp

${OBJECTDIR}/sources/MessageException.o: sources/MessageException.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/MessageException.o sources/MessageException.cpp

${OBJECTDIR}/sources/RequestHandler.o: sources/RequestHandler.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/RequestHandler.o sources/RequestHandler.cpp

${OBJECTDIR}/sources/RequestParser.o: sources/RequestParser.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/RequestParser.o sources/RequestParser.cpp

${OBJECTDIR}/sources/ResponseDispatcher.o: sources/ResponseDispatcher.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/ResponseDispatcher.o sources/ResponseDispatcher.cpp

${OBJECTDIR}/sources/ServerFramework.o: sources/ServerFramework.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/ServerFramework.o sources/ServerFramework.cpp

${OBJECTDIR}/sources/TCPConnectionManager.o: sources/TCPConnectionManager.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/TCPConnectionManager.o sources/TCPConnectionManager.cpp

${OBJECTDIR}/sources/main.o: sources/main.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/main.o sources/main.cpp

${OBJECTDIR}/sources/stdafx.o: sources/stdafx.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/stdafx.o sources/stdafx.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
