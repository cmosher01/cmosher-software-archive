// bindif.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "bindif.h"
#include "bindifDlg.h"

#include <stdio.h>

#include "dif.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CBindifApp

BEGIN_MESSAGE_MAP(CBindifApp, CWinApp)
	//{{AFX_MSG_MAP(CBindifApp)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CBindifApp construction

CBindifApp::CBindifApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CBindifApp object

CBindifApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CBindifApp initialization

BOOL CBindifApp::InitInstance()
{
	gendif("file1.dat","file2.dat","file_dif.dat",2,64);

	applydif("file1.dat","file_dif.dat","file2n.dat");

	return FALSE;
}
