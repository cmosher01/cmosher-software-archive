#if !defined(AFX_PASSWORD_H__969049A5_89A3_11D4_9E6A_0000F1112C95__INCLUDED_)
#define AFX_PASSWORD_H__969049A5_89A3_11D4_9E6A_0000F1112C95__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// Password.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CPassword dialog

class CPassword : public wxDialog
{
// Construction
public:
	CPassword(wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CPassword)
	enum { IDD = IDD_PWD };
	wxString	m_strOldPwd;
	wxString	m_Pwd1;
	wxString	m_Pwd2;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CPassword)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CPassword)
		// NOTE: the ClassWizard will add member functions here
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_PASSWORD_H__969049A5_89A3_11D4_9E6A_0000F1112C95__INCLUDED_)
