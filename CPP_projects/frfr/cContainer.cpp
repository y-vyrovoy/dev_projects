#include "stdafx.h"
#include "cContainer.h"
#include <iterator>

static UINT uiContaierCounter = 0;

cContainer::cContainer()
{
	uiID = uiContaierCounter++;

	CString sDebugMessage;
	sDebugMessage.Format(_T("C(){uiID = %d}\r\n"), uiID);
	OutputDebugString(sDebugMessage);
}

cContainer::cContainer(cData datElement)
{
	m_lstData.push_back(new cData(datElement));
	uiID = uiContaierCounter++;

	CString sDebugMessage;
	sDebugMessage.Format(_T("C(X){uiID = %d}\r\n"), uiID);
	OutputDebugString(sDebugMessage);
}

cContainer::cContainer(cContainer & datElement)
{
	for (cData * p : datElement.m_lstData)
	{
		m_lstData.push_back(new cData(*p));
	}
	uiID = uiContaierCounter++;

	CString sDebugMessage;
	sDebugMessage.Format(_T("C(&X){uiID = %d}\r\n"), uiID);
	OutputDebugString(sDebugMessage);

}

cContainer::cContainer(cContainer && datElement)
{
	for (auto it = datElement.m_lstData.begin(); it != datElement.m_lstData.end(); it++)
	{
		m_lstData.push_back(*it);
		*it = NULL;
	}

	uiID = uiContaierCounter++;

	CString sDebugMessage;
	sDebugMessage.Format(_T("C(&&X){uiID = %d}\r\n"), uiID);
	OutputDebugString(sDebugMessage);

}

cContainer& cContainer::operator= (cContainer & datElement)
{
	for (cData *p : m_lstData)
	{
		delete p;
	}

	for (cData * p : datElement.m_lstData)
	{
		m_lstData.push_back(new cData(*p));
	}

	CString sDebugMessage;
	sDebugMessage.Format(_T("Op=(&X){uiID = %d}\r\n"), uiID);
	OutputDebugString(sDebugMessage);

	return *this;
}

cContainer& cContainer::operator= (cContainer && datElement)
{
	for (cData *p : m_lstData)
	{
		delete p;
	}
	m_lstData.clear();

	for (auto it = datElement.m_lstData.begin(); it != datElement.m_lstData.end(); it++)
	{
		m_lstData.push_back(*it);
		*it = NULL;
	}

	CString sDebugMessage;
	sDebugMessage.Format(_T("Op=(&&X){uiID = %d}\r\n"), uiID);
	OutputDebugString(sDebugMessage);

	return *this;
}


cContainer::~cContainer()
{
	for (cData *p : m_lstData)
	{
		delete p;
	}
}

void cContainer::add(int nVal)
{
	m_lstData.push_back(new cData(nVal));
}

void cContainer::add(cData const& dat)
{
	m_lstData.push_back(new cData(dat));
}

void cContainer::FlushToDebugOutput()
{
	CString sMessage;

	for (cData* p : m_lstData)
	{
		CString sFormat;
		if (sMessage.IsEmpty() == true)
		{
			sFormat = _T("%s[%d, %d]");
		}
		else
		{
			sFormat = _T("%s, [%d, %d]");
		}

		sMessage.Format(sFormat, sMessage, p->GetID(), p->GetValue());
	}

	sMessage.Format(_T("Container #%d: %s"), uiID, sMessage);
	OutputDebugString(sMessage + _T("\r\n"));
}


cContainer getOddContainer()
{
	cContainer ret;
	for (int i = 0; i < 5; i++)
	{
		ret.add(1 + i * 2);
	}

	return ret;
}

cContainer getEvenContainer()
{
	cContainer ret;
	for (int i = 0; i < 5; i++)
	{
		ret.add(i * 2);
	}

	return ret;
}

