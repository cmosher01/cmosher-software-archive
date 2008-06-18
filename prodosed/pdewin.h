// pdewin.h : main header file for the PDEWIN application
//

#if !defined(AFX_PDEWIN_H__56A05086_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
#define AFX_PDEWIN_H__56A05086_25F6_11D3_9A02_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"       // main symbols

const int BYTES_PER_BLOCK(0x200);
const int BYTES_PER_SECTOR(0x100);
const int SECTORS_PER_TRACK(0x10);

typedef BYTE block[BYTES_PER_BLOCK];
typedef BYTE sector[BYTES_PER_SECTOR];
typedef sector track[SECTORS_PER_TRACK];

enum osType { osDos33, osProdos };

enum UM
{
	UM_DRAGGED_TO = WM_USER, // LPARAM is point(x,y) (screen-coords) dragged to
	UM_DRAGGED_OFF,
	UM_DROPPED_FILE,
	UM_DROPPED_DIR
};

template<class T>
T CeilDiv(T n, T d) { return (n+d-1)/d; }

/////////////////////////////////////////////////////////////////////////////
// CPdewinApp:
// See pdewin.cpp for the implementation of this class
//

class CPdewinApp : public CWinApp
{
public:
	CPdewinApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CPdewinApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation
	//{{AFX_MSG(CPdewinApp)
	afx_msg void OnAppAbout();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

extern CPdewinApp theApp;

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_PDEWIN_H__56A05086_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
