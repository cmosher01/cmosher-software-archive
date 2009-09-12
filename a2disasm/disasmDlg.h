// disasmDlg.h : header file
//

#if !defined(AFX_DISASMDLG_H__2995FDE6_225C_11D3_8CE0_00A0C9C95296__INCLUDED_)
#define AFX_DISASMDLG_H__2995FDE6_225C_11D3_8CE0_00A0C9C95296__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CDisasmDlg dialog

class CDisasmDlg : public CDialog
{
// Construction
public:
	CDisasmDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CDisasmDlg)
	enum { IDD = IDD_DISASM_DIALOG };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CDisasmDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	//{{AFX_MSG(CDisasmDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_DISASMDLG_H__2995FDE6_225C_11D3_8CE0_00A0C9C95296__INCLUDED_)
