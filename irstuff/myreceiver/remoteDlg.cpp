// remoteDlg.cpp : implementation file
//

#include "stdafx.h"
#include "remote.h"
#include "remoteDlg.h"
#include "Timer.h"
#include "Parser.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CRemoteDlg dialog

CRemoteDlg::CRemoteDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CRemoteDlg::IDD, pParent), m_com(1)
{
	//{{AFX_DATA_INIT(CRemoteDlg)
	m_strProduct = _T("");
	m_strCommand = _T("");
	m_nMaxSig = 2020;
	m_nMaxSht = 1035;
	m_nPulse = 0;
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CRemoteDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CRemoteDlg)
	DDX_Text(pDX, IDC_TITLE, m_strProduct);
	DDX_Text(pDX, IDC_COMMAND, m_strCommand);
	DDX_Text(pDX, IDC_MAXSIG, m_nMaxSig);
	DDX_Text(pDX, IDC_MAXSHT, m_nMaxSht);
	DDX_Text(pDX, IDC_PULSE, m_nPulse);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CRemoteDlg, CDialog)
	//{{AFX_MSG_MAP(CRemoteDlg)
	ON_BN_CLICKED(IDC_RECORD, OnRecord)
	ON_BN_CLICKED(IDC_TEST, OnTest)
	ON_BN_CLICKED(IDC_RTSOFF, OnRtsoff)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CRemoteDlg message handlers

BOOL CRemoteDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	m_com.Open();

	CString str;
	LPTSTR sApp = str.GetBuffer(MAX_PATH+1);
	::GetModuleFileName(NULL,sApp,MAX_PATH+1);
	str.ReleaseBuffer();

	TCHAR drive[_MAX_DRIVE];
	TCHAR dir[_MAX_DIR];
	TCHAR fname[_MAX_FNAME];
	TCHAR ext[_MAX_EXT];
	_tsplitpath(str,drive,dir,fname,ext);

	m_strLogFile = drive;
	m_strLogFile += dir;
	m_strLogFile += fname;
	m_strIniFile = m_strLogFile;

	m_strLogFile += ".LOG";
	m_strIniFile += ".INI";

	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CRemoteDlg::OnRecord() 
{
	try
	{
		DoRecord();
	}
	catch (const char* s)
	{
		HandleError(s);
	}
	catch (const CString& s)
	{
		HandleError(s);
	}
	CDialog::OnOK();
}

void CRemoteDlg::HandleError(const CString& str) 
{
	CString s;
	s.Format("The following error has occurred: %s.",(LPCTSTR)str);
	AfxMessageBox(s);
}

void CRemoteDlg::DoRecord() 
{
	CStdioFile f(m_strLogFile,CFile::modeCreate|CFile::modeWrite|CFile::modeNoTruncate);
	f.SeekToEnd();
	CParser parser;

	BOOL bEverGotAPulse(FALSE);

	if (!::SetPriorityClass(::GetCurrentProcess(),REALTIME_PRIORITY_CLASS))
		throw "Error setting priority class.";

	if (!::SetThreadPriority(::GetCurrentThread(),THREAD_PRIORITY_TIME_CRITICAL))
		throw "Error setting priority.";

	m_com.Pulse(TRUE);// RTS "on" (+10V) (power-on device)

	// clear status
	while (m_com.IsPulse())
		::Sleep(100);

	BOOL bWasPulse = m_com.IsPulse();
	CTimer timer;
	while (timer.Peek()<2000000 || !bEverGotAPulse)
	{
		BOOL bIsPulse = m_com.IsPulse();
//		BOOL bIsPulse = m_com.WaitForChange();
		if (bIsPulse!=bWasPulse)
		{
			int uS = timer.Checkpoint();
			if (!bEverGotAPulse)
				bEverGotAPulse = TRUE;
			else
				parser.Add(bWasPulse,uS);
			bWasPulse = bIsPulse;
		}
//		if (bIsPulse==bWasPulse)
//			throw "Error: out of synch with device";
	}

	m_com.Pulse(FALSE);// RTS "off" (-10V) (power-off device)

	UpdateData();
	CString str;
	str.Format("[%s]\n%s\n\n",
		(LPCTSTR)m_strProduct,
		(LPCTSTR)m_strCommand);
	f.WriteString(str);
	parser.SetParams(m_nMaxSig,m_nMaxSht,f);
	CString strCmd = parser.Parse();
	f.Flush();
	WriteCommandToIniFile(strCmd);
}

void CRemoteDlg::WriteCommandToIniFile(const CString& str)
{
	if (!::WritePrivateProfileString(
		m_strProduct,  // pointer to section name
		m_strCommand,  // pointer to key name
		str,   // pointer to string to add
		m_strIniFile  // pointer to initialization filename
	))
		throw "Couldn't write .INI file entry.";
}

void CRemoteDlg::OnTest() 
{
	UpdateData();

	if (!::SetPriorityClass(::GetCurrentProcess(),REALTIME_PRIORITY_CLASS))
		throw "Error setting priority class.";

	if (!::SetThreadPriority(::GetCurrentThread(),THREAD_PRIORITY_TIME_CRITICAL))
		throw "Error setting priority.";

/*
	m_com.Pulse(TRUE,250000);
	m_com.Pulse(FALSE,500000);
	m_com.Pulse(TRUE,250000);
	m_com.Pulse(FALSE,500000);
	m_com.Pulse(TRUE,250000);
*/
/*
	for (int i(10); i<500; i += 10)
	{
		m_com.Pulse(TRUE,i);
		m_com.Pulse(FALSE,500);
	}
*/
//	m_com.Pulse(TRUE,1000000);
//	for (int i(0); i<15; i++)
//	{
//		m_com.Pulse(FALSE,1000000);
//		m_com.Pulse(TRUE,1000000);
//	}
/*
	// jvc receiver volume+
	//1100010101111000
	m_com.Pulse(TRUE,8531);
	m_com.Pulse(FALSE,4103);
	for (int i(0); i<10; i++)
	{
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,1472);//1
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,419);//0
		m_com.Pulse(TRUE,636);
		m_com.Pulse(FALSE,20000);
	}
*/


//P650,0S390,1S1446
	// jvc vcr power
	//1100001011010000
	m_com.Pulse(TRUE,8546);
	m_com.Pulse(FALSE,4097);
	for (int i(0); i<10; i++)
	{
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,1446);//1
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,1446);//1
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,1446);//1
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,1446);//1
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,1446);//1
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,1446);//1
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,390);//0
		m_com.Pulse(TRUE,650);
		m_com.Pulse(FALSE,21751);
	}

/*
	// jvc vcr 3
	//1100001011000100
	m_com.Pulse(TRUE,8531);
	m_com.Pulse(FALSE,4064);
	for (int i(0); i<2; i++)
	{
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,1155);//1
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,1155);//1
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,1155);//1
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,1155);//1
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,1155);//1
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,1155);//1
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,370);//0
		m_com.Pulse(TRUE,557);
		m_com.Pulse(FALSE,21756);
	}
*/
	m_com.Pulse(FALSE);
}

void CRemoteDlg::OnRtsoff() 
{
	m_com.Pulse(FALSE);
}
