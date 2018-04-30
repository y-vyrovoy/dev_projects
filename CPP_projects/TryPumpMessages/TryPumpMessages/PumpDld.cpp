// PumpDld.cpp : implementation file
//

#include "stdafx.h"
#include "TryPumpMessages.h"
#include "PumpDld.h"
#include "afxdialogex.h"
#include "TryPumpMessagesDlg.h"


// CPumpDld dialog

IMPLEMENT_DYNAMIC(CPumpDld, CDialog)

CPumpDld::CPumpDld(CWnd* pParent /*=NULL*/)
	: CDialog(IDD_PUMPDLD, pParent)
{
	m_pRealParent = NULL;
}

CPumpDld::~CPumpDld()
{
}

void CPumpDld::SetRealParent(CTryPumpMessagesDlg* pParent)
{
	m_pRealParent = pParent;
}


void CPumpDld::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_EDIT1, m_edtText);
}


BEGIN_MESSAGE_MAP(CPumpDld, CDialog)
	ON_BN_CLICKED(IDC_BUTTON1, &CPumpDld::OnBnClickedButton1)
	ON_WM_TIMER()
	ON_WM_CREATE()
END_MESSAGE_MAP()


// CPumpDld message handlers


void CPumpDld::OnBnClickedButton1()
{
	if (m_pRealParent != NULL)
	{
		CString sEdtText;
		m_edtText.GetWindowText(sEdtText);
		OutputDebugString(sEdtText + "\r\n");

		m_pRealParent->m_txtStatic.SetWindowText(sEdtText);
	}
}


void CPumpDld::OnTimer(UINT_PTR nIDEvent)
{
	if (nIDEvent == IDT_TIMER_1)
	{
		PumpMessages();
	}

	CDialog::OnTimer(nIDEvent);
}

void CPumpDld::PumpMessages()
{
	MSG msg;
	BOOL bRet;

	while(PeekMessage(&msg, NULL, 0, 0, PM_REMOVE))
	{
		TranslateMessage(&msg);
		DispatchMessage(&msg);
	}
}

int CPumpDld::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
	if (CDialog::OnCreate(lpCreateStruct) == -1)
		return -1;

	
	SetTimer(IDT_TIMER_1, 100, NULL);
	return 0;
}
