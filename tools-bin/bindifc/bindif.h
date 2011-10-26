// bindif.h : main header file for the BINDIF application
//

#if !defined(AFX_BINDIF_H__3F4EB1A8_7930_11D4_9E6A_0000F1112C95__INCLUDED_)
#define AFX_BINDIF_H__3F4EB1A8_7930_11D4_9E6A_0000F1112C95__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CBindifApp:
// See bindif.cpp for the implementation of this class
//

class CBindifApp : public CWinApp
{
public:
	CBindifApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CBindifApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CBindifApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_BINDIF_H__3F4EB1A8_7930_11D4_9E6A_0000F1112C95__INCLUDED_)
