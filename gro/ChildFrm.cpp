// ChildFrm.cpp : implementation of the CChildFrame class
//

#include "stdafx.h"
#include "gedtree.h"

#include "ChildFrm.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CChildFrame

IMPLEMENT_DYNCREATE(CChildFrame, wxMDIChildFrame)

BEGIN_EVENT_TABLE(CChildFrame, wxMDIChildFrame)
	//{{AFX_MSG_MAP(CChildFrame)
	//}}AFX_MSG_MAP
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CChildFrame construction/destruction

CChildFrame::CChildFrame()
{
	// TODO: add member initialization code here
	
}

CChildFrame::~CChildFrame()
{
}

//BOOL CChildFrame::PreCreateWindow(CREATESTRUCT& cs)
//{
//	// TODO: Modify the Window class or styles here by modifying
//	//  the CREATESTRUCT cs
//
//	cs.style &= (LONG)~FWS_ADDTOTITLE;
//
//	return CMDIChildWnd::PreCreateWindow(cs);
//}

/////////////////////////////////////////////////////////////////////////////
// CChildFrame diagnostics

//#ifdef _DEBUG
//void CChildFrame::AssertValid() const
//{
//	CMDIChildWnd::AssertValid();
//}
//
//void CChildFrame::Dump(CDumpContext& dc) const
//{
//	CMDIChildWnd::Dump(dc);
//}
//
//#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CChildFrame message handlers
