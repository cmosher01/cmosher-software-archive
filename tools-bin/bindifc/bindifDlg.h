// bindifDlg.h : header file
//

#if !defined(AFX_BINDIFDLG_H__3F4EB1AA_7930_11D4_9E6A_0000F1112C95__INCLUDED_)
#define AFX_BINDIFDLG_H__3F4EB1AA_7930_11D4_9E6A_0000F1112C95__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CBindifDlg dialog

class CBindifDlg : public CDialog
{
// Construction
public:
	CBindifDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CBindifDlg)
	enum { IDD = IDD_BINDIF_DIALOG };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CBindifDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CBindifDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_BINDIFDLG_H__3F4EB1AA_7930_11D4_9E6A_0000F1112C95__INCLUDED_)
