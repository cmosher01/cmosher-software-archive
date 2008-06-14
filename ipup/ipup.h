// ipup.h : main header file for the IPUP application
//

#if !defined(AFX_IPUP_H__6E73DE55_17C9_409C_9615_AC49BF75AC0B__INCLUDED_)
#define AFX_IPUP_H__6E73DE55_17C9_409C_9615_AC49BF75AC0B__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CIpupApp:
// See ipup.cpp for the implementation of this class
//

class CIpupApp : public CWinApp
{
public:
	CIpupApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIpupApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	CString GetWebPage(const CString& strURL, const CString& strFormData = CString(), const CString& strUser = CString(), const CString& strPass = CString());

	//{{AFX_MSG(CIpupApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_IPUP_H__6E73DE55_17C9_409C_9615_AC49BF75AC0B__INCLUDED_)
