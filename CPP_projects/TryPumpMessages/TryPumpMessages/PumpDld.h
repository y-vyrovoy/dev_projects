#pragma once
#include "afxwin.h"
#include "TryPumpMessagesDlg.h"

// CPumpDld dialog

class CPumpDld : public CDialog
{
	DECLARE_DYNAMIC(CPumpDld)

public:
	CPumpDld(CWnd* pParent = NULL);   // standard constructor
	virtual ~CPumpDld();

// Dialog Data
#ifdef AFX_DESIGN_TIME
	enum { IDD = IDD_PUMPDLD };
#endif

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	void PumpMessages();
	CTryPumpMessagesDlg * m_pRealParent;


	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnBnClickedButton1();
	CEdit m_edtText;
	afx_msg void OnTimer(UINT_PTR nIDEvent);
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);

	void SetRealParent(CTryPumpMessagesDlg* pParent);
};
