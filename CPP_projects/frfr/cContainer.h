#pragma once
#include <list>
#include "cData.h"


class cContainer
{
public:
	cContainer();
	cContainer(cData datElement);
	cContainer(cContainer & datElement);
	cContainer(cContainer && datElement);

	cContainer& operator= (cContainer & datElement);
	cContainer& operator= (cContainer && datElement);

	~cContainer();

	void add(int nVal);
	void add(cData const& dat);

	void FlushToDebugOutput();

protected:
	std::list<cData*> m_lstData;
	UINT uiID;
};

cContainer getOddContainer();
cContainer getEvenContainer();