// ipupDlg.h : header file
//

#if !defined(AFX_IPUPDLG_H__F464B269_1891_42B4_A352_802F0D377252__INCLUDED_)
#define AFX_IPUPDLG_H__F464B269_1891_42B4_A352_802F0D377252__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CIpupDlg dialog

class CIpupDlg : public CDialog
{
// Construction
public:
	CIpupDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CIpupDlg)
	enum { IDD = IDD_IPUP_DIALOG };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CIpupDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CIpupDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_IPUPDLG_H__F464B269_1891_42B4_A352_802F0D377252__INCLUDED_)
