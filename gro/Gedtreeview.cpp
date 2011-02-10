// gedtreeView.cpp : implementation of the CGedtreeView class
//

#include "stdafx.h"
#include "gedtree.h"
#include "gedtreeView.h"
#include "gedtreeDoc.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CGedtreeView

IMPLEMENT_DYNCREATE(CGedtreeView, CScrollView)

BEGIN_EVENT_TABLE(CGedtreeView, CScrollView)
	//{{AFX_MSG_MAP(CGedtreeView)
	//}}AFX_MSG_MAP
	// Standard printing commands
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CGedtreeView construction/destruction

CGedtreeView::CGedtreeView()
{
}

CGedtreeView::~CGedtreeView()
{
}

void CGedtreeView::OnInitialUpdate() 
{
	CScrollView::OnInitialUpdate();
	UpdateTitle();
}

void CGedtreeView::UpdateTitle()
{
	wxString sTitle;
	sTitle =  GetDocument()->GetTitle();
	sTitle += " - ";
	sTitle += GetWindowTitle();

	GetParent()->SetWindowText(sTitle);
}

wxString CGedtreeView::GetWindowTitle()
{
	wxASSERT(FALSE);
	return wxString();
}

void CGedtreeView::Reset(UINT flagsChanged)
{
	wxASSERT(FALSE);
}
