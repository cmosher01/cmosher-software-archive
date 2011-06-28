// ChooseFont.cpp : implementation file
//
#if 0
#include "stdafx.h"
#include "gedtree.h"
#include "ChooseFont.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CChooseFont

IMPLEMENT_DYNAMIC(CChooseFont, CFontDialog)

CChooseFont::CChooseFont(LPLOGFONT lplfInitial, DWORD dwFlags, wxDC* pdcPrinter, wxWindow* pParentWnd) : 
	CFontDialog(lplfInitial, dwFlags|CF_ENABLETEMPLATE, pdcPrinter, pParentWnd),
	m_bScale(true)
{
	m_cf.hInstance = ::AfxGetInstanceHandle();
	m_cf.lpTemplateName = MAKEINTRESOURCE(IDD_CHOOSEFONT);
}

BEGIN_EVENT_TABLE(CChooseFont, CFontDialog)
END_EVENT_TABLE()

BOOL CChooseFont::OnInitDialog() 
{
	BOOL bRet = CFontDialog::OnInitDialog();
	CheckDlgButton(IDC_SCALE,m_bScale);
	return bRet;
}

void CChooseFont::OnOK() 
{
	m_bScale = !!IsDlgButtonChecked(IDC_SCALE);
	CFontDialog::OnOK();
}
#endif
