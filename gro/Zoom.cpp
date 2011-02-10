// Zoom.cpp : implementation file
//

#include "stdafx.h"
#include "gedtree.h"
#include "Zoom.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CZoom dialog


CZoom::CZoom(wxWindow* pParent /*=NULL*/)
	: wxDialog(CZoom::IDD, pParent)
{
	//{{AFX_DATA_INIT(CZoom)
	m_nScale = 0;
	//}}AFX_DATA_INIT
}


void CZoom::DoDataExchange(CDataExchange* pDX)
{
	wxDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CZoom)
	DDX_Text(pDX, IDC_SCALE, m_nScale);
	//}}AFX_DATA_MAP
}


BEGIN_EVENT_TABLE(CZoom, wxDialog)
	//{{AFX_MSG_MAP(CZoom)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CZoom message handlers
