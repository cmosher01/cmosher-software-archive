// disasm.h : main header file for the DISASM application
//

#if !defined(AFX_DISASM_H__2995FDE4_225C_11D3_8CE0_00A0C9C95296__INCLUDED_)
#define AFX_DISASM_H__2995FDE4_225C_11D3_8CE0_00A0C9C95296__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CDisasmApp:
// See disasm.cpp for the implementation of this class
//

class CDisasmApp : public CWinApp
{
public:
	CDisasmApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CDisasmApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CDisasmApp)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_DISASM_H__2995FDE4_225C_11D3_8CE0_00A0C9C95296__INCLUDED_)
