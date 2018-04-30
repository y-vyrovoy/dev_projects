#pragma once



class cData
{
public:
	cData();
	cData(int nVal);
	cData(cData const& dat);

	UINT GetID() { return uiID; }
	int GetValue() { return m_nValue; }

protected:
	UINT uiID;
	int m_nValue;
};

