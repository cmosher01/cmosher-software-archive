#if !defined(AFX_REGISTER_H__ABD71F23_B8F8_11D5_9E6A_0000F1112C95__INCLUDED_)
#define AFX_REGISTER_H__ABD71F23_B8F8_11D5_9E6A_0000F1112C95__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// Register.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CRegister dialog

class CRegister : public wxDialog
{
// Construction
public:
	CRegister(wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CRegister)
	enum { IDD = IDD_REGISTER };
	wxString	m_strName;
	wxString	m_strKey;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CRegister)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CRegister)
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_REGISTER_H__ABD71F23_B8F8_11D5_9E6A_0000F1112C95__INCLUDED_)
