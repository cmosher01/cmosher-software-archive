// ToppsDlg.h : header file
//

#if !defined(AFX_TOPPSDLG_H__F9B6E556_7DBF_11D2_98F8_D049CF251E5B__INCLUDED_)
#define AFX_TOPPSDLG_H__F9B6E556_7DBF_11D2_98F8_D049CF251E5B__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CToppsDlg dialog

class CToppsDlg : public CDialog
{
// Construction
public:
	CToppsDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CToppsDlg)
	enum { IDD = IDD_TOPPS_DIALOG };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CToppsDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CToppsDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_TOPPSDLG_H__F9B6E556_7DBF_11D2_98F8_D049CF251E5B__INCLUDED_)
