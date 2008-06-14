// remote.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "remote.h"
#include "remoteDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CRemoteApp

BEGIN_MESSAGE_MAP(CRemoteApp, CWinApp)
	//{{AFX_MSG_MAP(CRemoteApp)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// The one and only CRemoteApp object

CRemoteApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CRemoteApp initialization

BOOL CRemoteApp::InitInstance()
{
	CRemoteDlg dlg;
	m_pMainWnd = &dlg;
	dlg.DoModal();

	return FALSE;
}
