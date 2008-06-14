// remoteDlg.h : header file
//

#if !defined(AFX_REMOTEDLG_H__918AF3F9_5303_11D2_8C4C_00A0C9C95296__INCLUDED_)
#define AFX_REMOTEDLG_H__918AF3F9_5303_11D2_8C4C_00A0C9C95296__INCLUDED_

#if _MSC_VER >= 1000
#pragma once
#endif // _MSC_VER >= 1000

#include "ComPort.h"

/////////////////////////////////////////////////////////////////////////////
// CRemoteDlg dialog

class CRemoteDlg : public CDialog
{
// Construction
public:
	CRemoteDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CRemoteDlg)
	enum { IDD = IDD_REMOTE_DIALOG };
	CString	m_strProduct;
	CString	m_strCommand;
	int		m_nMaxSig;
	int		m_nMaxSht;
	int		m_nPulse;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CRemoteDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;
	CComPort m_com;
	CString m_strLogFile;
	CString m_strIniFile;

	void HandleError(const CString& str);
	void DoRecord();
	void WriteCommandToIniFile(const CString& str);

	// Generated message map functions
	//{{AFX_MSG(CRemoteDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnRecord();
	afx_msg void OnTest();
	afx_msg void OnRtsoff();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Developer Studio will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_REMOTEDLG_H__918AF3F9_5303_11D2_8C4C_00A0C9C95296__INCLUDED_)
