// DlgSaveChanges.cpp : implementation file
//

#include "stdafx.h"
#include "resource.h"
#include "DlgSaveChanges.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CDlgSaveChanges dialog


CDlgSaveChanges::CDlgSaveChanges(wxString& strFileName, wxWindow* pParent /*=NULL*/)
{
	//{{AFX_DATA_INIT(CDlgSaveChanges)
	//}}AFX_DATA_INIT
	m_strPrompt = strFileName;
	if (m_strPrompt.IsEmpty())
		m_strPrompt = _T("[untitled]");
	m_strPrompt += _T(" ?");
}


void CDlgSaveChanges::DoDataExchange(CDataExchange* pDX)
{
//	wxDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CDlgSaveChanges)
	DDX_Text(pDX, IDC_FILENAME, m_strPrompt);
	//}}AFX_DATA_MAP
}


BEGIN_EVENT_TABLE(CDlgSaveChanges, wxDialog)
	//{{AFX_MSG_MAP(CDlgSaveChanges)
//	ON_BN_CLICKED(IDYES, OnYes)
//	ON_BN_CLICKED(IDNO, OnNo)
	//}}AFX_MSG_MAP
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CDlgSaveChanges message handlers

void CDlgSaveChanges::OnYes() 
{
	EndDialog(IDYES);
}

void CDlgSaveChanges::OnNo() 
{
	EndDialog(IDNO);
}
