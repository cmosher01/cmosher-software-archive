// About.cpp : implementation file
//

#include "stdafx.h"
#include "gedtree.h"
#include "About.h"
#include "util.h"
#include "version.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAbout dialog


CAbout::CAbout(wxWindow* pParent /*=NULL*/)
{
	//{{AFX_DATA_INIT(CAbout)
	m_strAbout = _T("");
	//}}AFX_DATA_INIT
}


void CAbout::DoDataExchange(CDataExchange* pDX)
{
	//wxDialog::DoDataExchange(pDX);

	if (!pDX->m_bSaveAndValidate)
		m_strAbout = GetAbout();

	//{{AFX_DATA_MAP(CAbout)
	DDX_Control(pDX, IDC_ABOUT, m_staticAbout);
	DDX_Text(pDX, IDC_ABOUT, m_strAbout);
	//}}AFX_DATA_MAP

	if (!pDX->m_bSaveAndValidate)
	{
		LOGFONT lf;
		lf.lfHeight = -12;
		lf.lfWidth = 0;
		lf.lfEscapement = 0;
		lf.lfOrientation = 0;
		lf.lfWeight = FW_NORMAL;
		lf.lfItalic = 0;
		lf.lfUnderline = 0;
		lf.lfStrikeOut = 0;
		lf.lfCharSet = ANSI_CHARSET;
		lf.lfOutPrecision = OUT_DEFAULT_PRECIS;
		lf.lfClipPrecision = CLIP_DEFAULT_PRECIS;
		lf.lfQuality = PROOF_QUALITY;
		lf.lfPitchAndFamily = DEFAULT_PITCH;
		_tcscpy(lf.lfFaceName,_T("Arial"));
		m_staticAbout.SetFont(wxSystemSettings::GetFont(wxSYS_DEFAULT_GUI_FONT));
	}
}


BEGIN_EVENT_TABLE(CAbout, wxDialog)
	//{{AFX_MSG_MAP(CAbout)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CAbout message handlers

wxString CAbout::GetAbout()
{
	wxString s;

	s += ::wxGetApp().GetAppName(); s+= _T("\n");
	s += _T("Version "); s += ::wxGetApp().m_info.m_strVersion;
	s += _T("  (released ");
	s += RELEASE_DATE;
	s += _T(")\n");
	s += ::wxGetApp().m_info.m_strCopyright; s+= _T("\n");
	s += _T("\n");
	s += ::wxGetApp().m_info.m_strComments;

	s += _T("\n\nrunning: ");

	UINT nAppDriveType = ::wxGetApp().m_info.m_nAppDriveType;
	if (nAppDriveType==DRIVE_REMOVABLE)
		s += _T("(floppy) ");
	else if (nAppDriveType==DRIVE_CDROM)
		s += _T("(CD-ROM) ");
	else if (nAppDriveType==DRIVE_REMOTE)
		s += _T("(network) ");

	wxString strApp = ::wxGetApp().m_info.m_strAppPath;
	strApp.MakeLower();
	s += strApp;

	return s;
}
