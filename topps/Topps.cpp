// Topps.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include <afxtempl.h>
#include "Topps.h"
#include "ToppsDlg.h"
#include "sqlerr.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

static void DoDbCall();

/////////////////////////////////////////////////////////////////////////////
// CToppsApp

BEGIN_MESSAGE_MAP(CToppsApp, CWinApp)
	//{{AFX_MSG_MAP(CToppsApp)
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CToppsApp construction

CToppsApp::CToppsApp()
{
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CToppsApp object

CToppsApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CToppsApp initialization

BOOL CToppsApp::InitInstance()
{
	// Standard initialization

	CToppsDlg dlg;
	m_pMainWnd = &dlg;
	int nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
		DoDbCall();
	}
	else if (nResponse == IDCANCEL)
	{
	}

	// Since the dialog has been closed, return FALSE so that we exit the
	//  application, rather than start the application's message pump.
	return FALSE;
}

// Main entry point into the program.

void ds(long nErr);
BOOL got(long nErr);

extern "C" 
{

long _stdcall wsqlexec(
   char *command
);
long _stdcall wsqllasterror(
   char * buffer,
   int len
);
long _stdcall wsqlgetfield(
   char   * cname,
   int      offset,
   short   * ind,
   char   * result,
   int      len
);


}

/*
class HString : public CString
{
public:
	HString() {}
	HString(LPCTSTR s) : CString(s) {}
	CString operator =(LPCTSTR s) { return CString::operator =(s);}
	virtual ~HString() {}
	operator UINT()
	{
		LPCTSTR key = (LPCTSTR)this;
		UINT nHash = 0;
		while (*key)
			nHash = (nHash<<5) + nHash + *key++;
		return nHash;
	}
};
*/
class CQuery
{
	CMap<CString,LPCSTR,int,int> m_mapNameCol;
	CMap<CString,LPCSTR,CString,LPCSTR> m_mapNameVal;
public:
	CQuery();
	virtual ~CQuery();
	void Select(const CString& strSelect);
	BOOL Fetch();
	CString operator[](const CString& strCol);
};

CQuery::CQuery()
{
}

CQuery::~CQuery()
{
}

void CQuery::Select(const CString& strSelect)
{
	ds(wsqlexec((LPSTR)(LPCSTR)("PREPARE s FROM "+strSelect)));
	ds(wsqlexec("DECLARE c CURSOR FOR s"));
	ds(wsqlexec("OPEN c"));
	CString strLastSet, strLastSeries, strLastSub;
	ds(wsqlexec("CLIP NAMES c"));
	BOOL bOK = ::OpenClipboard(NULL);
	HANDLE hClip = ::GetClipboardData(CF_TEXT);
	CString strNames((LPCSTR)hClip);
	strNames = strNames.Left(strNames.GetLength()-2);
	int iCol = 0;
	while (!strNames.IsEmpty())
	{
		int n = strNames.Find('\t');
		CString strCol;
		if (n>=0)
		{
			strCol = strNames.Left(n);
			strNames = strNames.Mid(n+1);
		}
		else
		{
			strCol = strNames;
			strNames.Empty();
		}
		strCol.MakeUpper();
		m_mapNameCol[strCol] = iCol++;
		m_mapNameVal[strCol] = "";
	}
}

const int BUFLEN(1024);

BOOL CQuery::Fetch()
{
	if (!got(wsqlexec("FETCH c")))
		return FALSE;

	POSITION pos = m_mapNameCol.GetStartPosition();
	while (pos)
	{
		CString strName;
		int iCol;
		m_mapNameCol.GetNextAssoc(pos,strName,iCol);
		short ind;
		char buf[BUFLEN];
		ds(wsqlgetfield("c",iCol,&ind,buf,BUFLEN));
		m_mapNameVal[strName] = buf;
	}
	return TRUE;
}

CString CQuery::operator[](const CString& strCol)
{
	CString strColu = strCol;
	strColu.MakeUpper();
	CString strVal;
	if (!m_mapNameVal.Lookup(strColu,strVal))
		throw "Column not found.";

	return strVal;
}

/*
void DoDbCall()
{
	CStdioFile fil("Topps.rpt",CFile::modeCreate|CFile::modeWrite);
	ds(wsqlexec("START USING DATABASE Topps dba IDENTIFIED BY sql"));
	CQuery q;
	q.Select(
	"SELECT cards.number "
		",cards.title "
		",cards.title2 "
		",cards.cardseries "
		",cards.subseries "
		",cards.owned "
		",cards.yearmade "
		",cards.ts "
		",cards.pricemin "
		",cards.pricemax "
		",cards.sameas "
		",cards.distribution "
		",attr1.attrname attr1"
		",attr2.attrname attr2"
		",attr3.attrname attr3"
		",cardsets.cardname "
	"FROM cards "
	"key left outer join attr as attr1 "
	"key left outer join attr as attr2 "
	"key left outer join attr as attr3 "
	"key left outer join cardsets "
	"where cards.owned=0 and cards.regulartype=2 "
	"ORDER BY cards.cardset,cards.cardseries,cards.regulartype,cards.attr1,cards.attr2,cards.ts ");
	CString strLastSet, strLastSeries, strLastSub;
	while (q.Fetch())
	{
		CString sNumber = q["number"];
		CString sTitle= q["title"];
		CString sTitle2 = q["title2"];
		CString sSeries = q["cardseries"];
		CString sSubSeries = q["subseries"];
		CString sOwned = q["owned"];
		CString sYear = q["yearmade"];
		CString sTS = q["ts"];
		CString sPriceMin = q["pricemin"];
		CString sPriceMax = q["pricemax"];
		CString sSameAs = q["sameas"];
		CString sDistr = q["distribution"];
		CString sAttr1 = q["attr1"];
		CString sAttr2 = q["attr2"];
		CString sAttr3 = q["attr3"];
		CString sSet = q["cardname"];

		if (sSet!=strLastSet)
		{
			strLastSet = sSet;
			strLastSeries = "";
			fil.WriteString(sSet);
			fil.WriteString("\n");
		}
		if (sSeries!=strLastSeries)
		{
			strLastSeries = sSeries;
			strLastSub = "";
			long n = atoi(sSeries);
			if (n)
			{
				fil.WriteString("Series ");
				fil.WriteString(sSeries);
				fil.WriteString("\n");
			}
		}
		if (sSubSeries!=strLastSub)
		{
			strLastSub = sSubSeries;
			if (!sSubSeries.IsEmpty())
			{
				fil.WriteString(sSubSeries);
				fil.WriteString("\n");
			}
		}
		if (atoi(sOwned))
			fil.WriteString("x");
		fil.WriteString("\t");
		fil.WriteString(sYear);
		fil.WriteString("\t");
		if (!sTS.IsEmpty())
		{
			fil.WriteString("TS");
			fil.WriteString(sTS);
		}
		fil.WriteString("\t");
		fil.WriteString(sNumber);
		fil.WriteString("\t");
		if (atof(sPriceMin)>.00001)
		{
			fil.WriteString("$");
			fil.WriteString(sPriceMin);
			if (atof(sPriceMax)>.00001)
			{
				fil.WriteString("-$");
				fil.WriteString(sPriceMax);
			}
			else
			{
				fil.WriteString("+");
			}
		}
		fil.WriteString("\t");
		fil.WriteString(sTitle);
		if (!sTitle2.IsEmpty())
		{
			fil.WriteString("/");
			fil.WriteString(sTitle2);
		}
		if (!sSameAs.IsEmpty())
		{
			fil.WriteString("(same as ");
			fil.WriteString(sSameAs);
			fil.WriteString(")");
		}
		fil.WriteString("\t");
		fil.WriteString(sDistr);
		fil.WriteString("\t");
		if (!sAttr1.IsEmpty())
			fil.WriteString(sAttr1.Left(2));
		if (!sAttr2.IsEmpty())
			fil.WriteString(sAttr2.Left(2));
		if (!sAttr3.IsEmpty())
			fil.WriteString(sAttr3.Left(2));
		fil.WriteString("\n");
	}
}
*/

void ds(long nErr)
{
	if (!nErr)
		return;

	CString str;
	wsqllasterror(str.GetBuffer(1023),1024);
	str.ReleaseBuffer();
	AfxMessageBox(str);
	throw str;
}

CString TD()
{
	return "<TD><FONT SIZE=2>";
}

CString TDend()
{
	return "</FONT></TD>\n";
}

CString FormatNumber(const CString& str)
{
	if (str.SpanIncluding("0123456789")==str)
	{
		if (str.GetLength()<3)
			return CString(' ',3-str.GetLength())+str;
		else
			return str;
	}

	return str;
}

CString IfExists(const CString& str)
{
	if (str.IsEmpty())
		return "&nbsp;";

	return str;
}

void DoSet(int nSet);
void DoType(int nSet, int nType);

void DoDbCall()
{
	for (int i(1); i<16; i++)
		DoSet(i);
}

void DoSet(int nSet)
{
	for (int i(0); i<4; i++)
		DoType(nSet,i);
}

void DoType(int nSet, int nType)
{
	CString strType;
	switch (nType)
	{
		case 0:
			strType = "b";
		break;
		case 1:
			strType = "c";
		break;
		case 2:
			strType = "p";
		break;
		case 3:
			strType = "bv";
		break;
		default:
			ASSERT(FALSE);
	};

	CString strFileName;
	strFileName.Format("%2.2d%s.html",nSet,(LPCTSTR)strType);

	CStdioFile fil(strFileName,CFile::modeCreate|CFile::modeWrite);

	ds(wsqlexec("START USING DATABASE Topps dba IDENTIFIED BY sql"));

	CString strSelect;
	strSelect.Format(
	"SELECT cards.number "
		",cards.title "
		",cards.title2 "
		",cards.cardseries "
		",cards.subseries "
		",cards.owned "
		",cards.yearmade "
		",cards.ts "
		",cards.pricemin "
		",cards.pricemax "
		",cards.sameas "
		",cards.distribution "
		",attr1.attrname attr1"
		",attr2.attrname attr2"
		",attr3.attrname attr3"
		",cardsets.cardname "
	"FROM cards "
		"key left outer join attr as attr1 "
		"key left outer join attr as attr2 "
		"key left outer join attr as attr3 "
		"key left outer join cardsets "
	"WHERE cards.cardset=%d and cards.regulartype=%d "
	"ORDER BY cards.cardseries,cards.attr1,cards.attr2,cards.ts ",
	nSet,nType);

	CQuery q;
	q.Select(strSelect);

	CString strLastSet, strLastSeries, strLastSub;

	fil.WriteString("<HTML>\n");
	fil.WriteString("<HEAD>\n");
	fil.WriteString("<TITLE>");
	fil.WriteString("Topps"); //???
	fil.WriteString("</TITLE>\n");
	fil.WriteString("</HEAD>\n");
	fil.WriteString("<BODY>\n");
	fil.WriteString("<TABLE BORDER=1 CELLSPACING=0>\n");

	fil.WriteString("<TR>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Number");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Title");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Title 2");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Series");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Sub-Series");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Same as");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Distribution");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Attribute 1");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Attribute 2");
	fil.WriteString("</TH>\n");
	fil.WriteString("<TH>");
	fil.WriteString("Attribute 3");
	fil.WriteString("</TH>\n");
	fil.WriteString("</TR>\n");

	while (q.Fetch())
	{
		fil.WriteString("<TR>\n");

		CString sNumber = q["number"];
		CString sTitle= q["title"];
		CString sTitle2 = q["title2"];
		CString sSeries = q["cardseries"];
		CString sSubSeries = q["subseries"];
		CString sOwned = q["owned"];
		CString sYear = q["yearmade"];
		CString sTS = q["ts"];
		CString sPriceMin = q["pricemin"];
		CString sPriceMax = q["pricemax"];
		CString sSameAs = q["sameas"];
		CString sDistr = q["distribution"];
		CString sAttr1 = q["attr1"];
		CString sAttr2 = q["attr2"];
		CString sAttr3 = q["attr3"];
		CString sSet = q["cardname"];

		fil.WriteString(TD());
		fil.WriteString(IfExists(sNumber));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sTitle));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sTitle2));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sSeries));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sSubSeries));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sSameAs));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sDistr));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sAttr1));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sAttr2));
		fil.WriteString(TDend());
		fil.WriteString(TD());
		fil.WriteString(IfExists(sAttr3));
		fil.WriteString(TDend());
/*
		if (sSet!=strLastSet)
		{
			strLastSet = sSet;
			strLastSeries = "";
			fil.WriteString(sSet);
			fil.WriteString("\n");
		}
		if (sSeries!=strLastSeries)
		{
			strLastSeries = sSeries;
			strLastSub = "";
			long n = atoi(sSeries);
			if (n)
			{
				fil.WriteString("Series ");
				fil.WriteString(sSeries);
				fil.WriteString("\n");
			}
		}
		if (sSubSeries!=strLastSub)
		{
			strLastSub = sSubSeries;
			if (!sSubSeries.IsEmpty())
			{
				fil.WriteString(sSubSeries);
				fil.WriteString("\n");
			}
		}
		if (atoi(sOwned))
			fil.WriteString("x");
		fil.WriteString("\t");
		fil.WriteString(sYear);
		fil.WriteString("\t");
		if (!sTS.IsEmpty())
		{
			fil.WriteString("TS");
			fil.WriteString(sTS);
		}
		fil.WriteString("\t");
		fil.WriteString(sNumber);
		fil.WriteString("\t");
		if (atof(sPriceMin)>.00001)
		{
			fil.WriteString("$");
			fil.WriteString(sPriceMin);
			if (atof(sPriceMax)>.00001)
			{
				fil.WriteString("-$");
				fil.WriteString(sPriceMax);
			}
			else
			{
				fil.WriteString("+");
			}
		}
		fil.WriteString("\t");
		fil.WriteString(sTitle);
		if (!sTitle2.IsEmpty())
		{
			fil.WriteString("/");
			fil.WriteString(sTitle2);
		}
		if (!sSameAs.IsEmpty())
		{
			fil.WriteString("(same as ");
			fil.WriteString(sSameAs);
			fil.WriteString(")");
		}
		fil.WriteString("\t");
		fil.WriteString(sDistr);
		fil.WriteString("\t");
		if (!sAttr1.IsEmpty())
			fil.WriteString(sAttr1.Left(2));
		if (!sAttr2.IsEmpty())
			fil.WriteString(sAttr2.Left(2));
		if (!sAttr3.IsEmpty())
			fil.WriteString(sAttr3.Left(2));
*/
		fil.WriteString("\n");

		fil.WriteString("</TR>\n");
	}
	fil.WriteString("</TABLE>\n");
	fil.WriteString("</BODY>\n");
	fil.WriteString("</HTML>\n");
}

BOOL got(long nErr)
{
	if (!nErr)
		return TRUE;

	if (nErr==SQLE_NOTFOUND)
		return FALSE;

	ds(nErr);
	return FALSE;
}
