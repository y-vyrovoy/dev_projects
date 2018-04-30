#include "stdafx.h"
#include "cData.h"


static UINT uiCounter = 0;

cData::cData()
{
	uiID = uiCounter++;
	m_nValue = -1;
}

cData::cData(int nVal)
{
	uiID = uiCounter++;
	m_nValue = nVal;
}

cData::cData(cData const& dat)
{
	uiID = uiCounter++;
	m_nValue = dat.m_nValue;
}