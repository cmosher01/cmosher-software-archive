// journalDlg.h : header file
//

#if !defined(AFX_JOURNALDLG_H__98DEDE45_F229_11D3_821D_FA7D95C9834C__INCLUDED_)
#define AFX_JOURNALDLG_H__98DEDE45_F229_11D3_821D_FA7D95C9834C__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CJournalDlg dialog

class CJournalDlg : public CDialog
{
// Construction
public:
	CJournalDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CJournalDlg)
	enum { IDD = IDD_JOURNAL_DIALOG };
	CString	m_strEntry;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CJournalDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CJournalDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_JOURNALDLG_H__98DEDE45_F229_11D3_821D_FA7D95C9834C__INCLUDED_)
