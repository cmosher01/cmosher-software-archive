// TextDlg.cpp : implementation file
//

#include "stdafx.h"
#include "pdewin.h"
#include "TextDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CTextDlg dialog


CTextDlg::CTextDlg(const CString& strText, BOOL bFix, CWnd* pParent /*=NULL*/)
	: CDialog(CTextDlg::IDD, pParent),
	m_strText(strText)
{
	//{{AFX_DATA_INIT(CTextDlg)
	//}}AFX_DATA_INIT
	if (bFix)
		FixText();
}

void CTextDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CTextDlg)
	DDX_Control(pDX, IDC_TEXT, m_richText);
	//}}AFX_DATA_MAP
	if (pDX->m_bSaveAndValidate)
		m_strText = m_richText.GetText();
	else
		m_richText.SetText(m_strText);
}

void CTextDlg::FixText()
{
	CString str = m_strText;
	m_strText.Empty();
	for (int i(0); i<str.GetLength(); i++)
	{
		char c = str[i];
		c &= 0x7f;
		if (c=='\r')
			m_strText += "\r\n";
		else
			m_strText += c;
	}
}

BEGIN_MESSAGE_MAP(CTextDlg, CDialog)
	//{{AFX_MSG_MAP(CTextDlg)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CTextDlg message handlers
