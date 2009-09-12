//{{AFX_INCLUDES()
#include "richtext.h"
//}}AFX_INCLUDES
#if !defined(AFX_TEXTDLG_H__D18323F1_298B_11D3_8CEE_00A0C9C95296__INCLUDED_)
#define AFX_TEXTDLG_H__D18323F1_298B_11D3_8CEE_00A0C9C95296__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// TextDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CTextDlg dialog

class CTextDlg : public CDialog
{
// Construction
public:
	CTextDlg(const CString& strText, BOOL bFix = TRUE, CWnd* pParent = NULL);

// Dialog Data
	//{{AFX_DATA(CTextDlg)
	enum { IDD = IDD_TEXT };
	CRichText	m_richText;
	//}}AFX_DATA
	CString m_strText;


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CTextDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	void FixText();

	// Generated message map functions
	//{{AFX_MSG(CTextDlg)
		// NOTE: the ClassWizard will add member functions here
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_TEXTDLG_H__D18323F1_298B_11D3_8CEE_00A0C9C95296__INCLUDED_)
