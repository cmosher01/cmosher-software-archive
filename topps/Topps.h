// Topps.h : main header file for the TOPPS application
//

#if !defined(AFX_TOPPS_H__F9B6E554_7DBF_11D2_98F8_D049CF251E5B__INCLUDED_)
#define AFX_TOPPS_H__F9B6E554_7DBF_11D2_98F8_D049CF251E5B__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CToppsApp:
// See Topps.cpp for the implementation of this class
//

class CToppsApp : public CWinApp
{
public:
	CToppsApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CToppsApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CToppsApp)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_TOPPS_H__F9B6E554_7DBF_11D2_98F8_D049CF251E5B__INCLUDED_)
