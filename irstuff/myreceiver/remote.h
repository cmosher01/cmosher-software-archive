// remote.h : main header file for the REMOTE application
//

#if !defined(AFX_REMOTE_H__918AF3F7_5303_11D2_8C4C_00A0C9C95296__INCLUDED_)
#define AFX_REMOTE_H__918AF3F7_5303_11D2_8C4C_00A0C9C95296__INCLUDED_

#if _MSC_VER >= 1000
#pragma once
#endif // _MSC_VER >= 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CRemoteApp:
// See remote.cpp for the implementation of this class
//

class CRemoteApp : public CWinApp
{
public:
	CRemoteApp() {}
	virtual ~CRemoteApp() {}

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CRemoteApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CRemoteApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Developer Studio will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_REMOTE_H__918AF3F7_5303_11D2_8C4C_00A0C9C95296__INCLUDED_)
