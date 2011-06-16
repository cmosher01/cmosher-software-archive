// disasm.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "disasm.h"
#include "disasmDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

static void test();

/////////////////////////////////////////////////////////////////////////////
// CDisasmApp

BEGIN_MESSAGE_MAP(CDisasmApp, CWinApp)
	//{{AFX_MSG_MAP(CDisasmApp)
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CDisasmApp construction

CDisasmApp::CDisasmApp()
{
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CDisasmApp object

CDisasmApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CDisasmApp initialization

BOOL CDisasmApp::InitInstance()
{
	// Standard initialization

	CDisasmDlg dlg;
	m_pMainWnd = &dlg;
	int nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
		test();
	}
	else if (nResponse == IDCANCEL)
	{
	}

	// Since the dialog has been closed, return FALSE so that we exit the
	//  application, rather than start the application's message pump.
	return FALSE;
}

CString disasm(int nBaseAddress, BYTE rBuffer[], int nBufferSize);

void test()
{
	CFile f("a:\\diskread\\BLOCKLOAD",CFile::modeRead);
	int n = f.GetLength();
	BYTE* pBuffer = new BYTE[n];
	f.Read(pBuffer,n);
	f.Close();
	CString s = disasm(0x800,pBuffer,n);
	delete [] pBuffer;
	CStdioFile g("a:\\dis.txt",CFile::modeWrite|CFile::modeCreate);
	g.WriteString(s);
}

static CString rsFormat[0x100];
static int rnArgs[0x100];

void fill()
{
#define inst(x,format,y,byte,nargs) rsFormat[byte] = format; rnArgs[byte] = nargs;
#include "instr.h"
}

CString disasm(int nBaseAddress, BYTE rBuffer[], int nBufferSize)
{
	fill();

	CString sProg;
	int pc(nBaseAddress);
	int i(0);
	while (i<nBufferSize)
	{
		CString s;
		int n = rBuffer[i];
		int nargs = rnArgs[n];
		if (nargs==1)
			s.Format("%4.4X\t"+rsFormat[n]+"\n",pc,rBuffer[i+1]);
		else if (nargs==2)
			s.Format("%4.4X\t"+rsFormat[n]+"\n",pc,rBuffer[i+2],rBuffer[i+1]);
		else if (nargs==0)
			s.Format("%4.4X\t"+rsFormat[n]+"\n",pc);
		else if (nargs==3)
		{
			nargs = 1;
			char off = (char)rBuffer[i+1];
			ASSERT(-0x80<=off && off<0x80);
			s.Format("%4.4X\t"+rsFormat[n]+"\n",pc,pc+nargs+1+off,rBuffer[i+1]);
		}
		else
			ASSERT(FALSE);

		sProg += s;
		pc += nargs+1; i += nargs+1;
	}
	return sProg;
}
