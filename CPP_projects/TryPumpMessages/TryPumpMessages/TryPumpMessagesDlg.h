
// TryPumpMessagesDlg.h : header file
//

#pragma once
#include "afxwin.h"


// CTryPumpMessagesDlg dialog
class CTryPumpMessagesDlg : public CDialogEx
{
// Construction
public:
	CTryPumpMessagesDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
#ifdef AFX_DESIGN_TIME
	enum { IDD = IDD_TRYPUMPMESSAGES_DIALOG };
#endif

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
	CStatic m_txtStatic;
	afx_msg void OnBnClickedButton1();
	afx_msg void OnBnClickedButton2();
};
