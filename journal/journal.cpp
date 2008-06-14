// journal.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "journal.h"
#include "journalDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CJournalApp

BEGIN_MESSAGE_MAP(CJournalApp, CWinApp)
	//{{AFX_MSG_MAP(CJournalApp)
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CJournalApp construction

CJournalApp::CJournalApp()
{
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CJournalApp object

CJournalApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CJournalApp initialization

BOOL CJournalApp::InitInstance()
{
	// Standard initialization

	CJournalDlg dlg;
	m_pMainWnd = &dlg;
	int nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
		CString strText = Clean(dlg.m_strEntry);
		CTime time = CTime::GetCurrentTime();
		CString strEntry =
			time.Format("%Y/%m/%d")+"\t"+
			time.Format("%H:%M")+"\t"+
			strText+
			"\n";
		try
		{
			CStdioFile file("Journal.txt",CFile::modeRead);
		}
		catch (CFileException* pFile)
		{
			pFile->Delete();
			CStdioFile file("Journal.txt",CFile::modeCreate);
		}
		CStdioFile file("Journal.txt",CFile::modeWrite|CFile::modeNoTruncate);
		file.SeekToEnd();
		file.WriteString(strEntry);
	}
	else if (nResponse == IDCANCEL)
	{
	}

	// Since the dialog has been closed, return FALSE so that we exit the
	//  application, rather than start the application's message pump.
	return FALSE;
}

CString CJournalApp::Clean(const CString& str)
{
	CString strOut;
	for (int i(0); i<str.GetLength(); i++)
	{
		char c = str[i];
		if (c<32 || 127<c)
			c = ' ';
		strOut += c;
	}
	return strOut;
}
