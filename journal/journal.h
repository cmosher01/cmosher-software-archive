// journal.h : main header file for the JOURNAL application
//

#if !defined(AFX_JOURNAL_H__98DEDE43_F229_11D3_821D_FA7D95C9834C__INCLUDED_)
#define AFX_JOURNAL_H__98DEDE43_F229_11D3_821D_FA7D95C9834C__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CJournalApp:
// See journal.cpp for the implementation of this class
//

class CJournalApp : public CWinApp
{
public:
	CJournalApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CJournalApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CJournalApp)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()

	CString Clean(const CString& str);
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_JOURNAL_H__98DEDE43_F229_11D3_821D_FA7D95C9834C__INCLUDED_)
