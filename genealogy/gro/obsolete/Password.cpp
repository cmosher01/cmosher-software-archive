// Password.cpp : implementation file
//

#include "stdafx.h"
#include "gedtree.h"
#include "Password.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CPassword dialog


CPassword::CPassword(wxWindow* pParent /*=NULL*/)
	: wxDialog(CPassword::IDD, pParent)
{
	//{{AFX_DATA_INIT(CPassword)
	m_strOldPwd = _T("");
	m_Pwd1 = _T("");
	m_Pwd2 = _T("");
	//}}AFX_DATA_INIT
}


void CPassword::DoDataExchange(CDataExchange* pDX)
{
	wxDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CPassword)
	DDX_Text(pDX, IDC_OLDPWD, m_strOldPwd);
	DDX_Text(pDX, IDC_PWD1, m_Pwd1);
	DDX_Text(pDX, IDC_PWD2, m_Pwd2);
	//}}AFX_DATA_MAP
	if (pDX->m_bSaveAndValidate)
	{
		if (m_strOldPwd != theApp.m_strPassword)
		{
			AfxMessageBox(L"You did not enter the correct old password.");
			pDX->Fail();
		}
		if (m_Pwd1 != m_Pwd2)
		{
			AfxMessageBox(L"The \"new password\" did not match the \"confirm new password.\"");
			pDX->Fail();
		}
		theApp.m_strPassword = m_Pwd1;
		if (!theApp.m_info.m_bPermanent)
			theApp.PutReg("Password","Password",theApp.m_strPassword);
	}
}


BEGIN_EVENT_TABLE(CPassword, wxDialog)
	//{{AFX_MSG_MAP(CPassword)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CPassword message handlers
