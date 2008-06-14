#include "stdafx.h"
#include "Parser.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

struct Dur
{
	int uS;
	int c;
};

static CString BinStr(unsigned int nCommand, int cBits = 32);
static CString Mirror(const CString& str);
static void AddToMap(CMap<int,int,int,int>& map, int uS);
static void AddToCmdMap(CMap<MyString,MyString&,int,int>& map, MyString& strCmd);
static void SortMap(CMap<int,int,int,int>& map, Dur rDur[]);
static void ShowDistribution(CMap<int,int,int,int>& map, CStdioFile* pf, CString& strCutoffs);
static void FilterMap(CMap<int,int,int,int>& map);
static void AddBit(unsigned int& nCommand, int nThisBit);
static void NextCommand(unsigned int& nCommand, int& cBits, CStdioFile* pf, CMap<MyString,MyString&,int,int>& mapCmd);
static void PrintCommandSummary(CMap<MyString,MyString&,int,int>& map, CStdioFile* pf);

CParser::CParser():
	m_cPulse(0),
	m_bShift(FALSE),
	m_bPulse(FALSE),
	m_bSpace(FALSE)
{
	m_rPulse.SetSize(15000,15000);
}

CParser::~CParser()
{
}

void CParser::Add(BOOL bPulse, int uS)
{
	Pulse p(bPulse,uS);
	m_rPulse.SetAtGrow(m_cPulse++,p);
}

CString CParser::Parse()
{
	int i;

	m_rPulse.SetSize(m_cPulse);

	CMap<int,int,int,int> /*uS->count*/ mapPulse, mapSpace;
	for (i = 0; i<m_rPulse.GetSize(); i++)
	{
		Pulse& p = m_rPulse[i];
		AddToMap(p.bPulse?mapPulse:mapSpace,p.uS);
	}
	pf->WriteString("Full distribution of durations:\n");
	pf->WriteString("  pulses:\n");
	CString strCutoffsPulse;
	ShowDistribution(mapPulse,pf,strCutoffsPulse);
	pf->WriteString("  spaces:\n");
	CString strCutoffsSpace;
	ShowDistribution(mapSpace,pf,strCutoffsSpace);
	pf->WriteString("\n");

	pf->WriteString("Summary of cutoffs for durations:\n");
	pf->WriteString("  pulses:\n");
	pf->WriteString(strCutoffsPulse);
	pf->WriteString("  spaces:\n");
	pf->WriteString(strCutoffsSpace);
	pf->WriteString("\n");

	pf->WriteString("Full dump of received pulses and spaces for entire session:\n");
	for (i = 0; i<m_rPulse.GetSize(); i++)
	{
		Pulse& p = m_rPulse[i];
		CString strTag;
		if (p.bPulse)
		{
			if (p.uS>m_nMaxSig)
				strTag = "---HEADER---";
			else if (p.uS>m_nMaxSht)
				strTag = "XXXXXXXXXXXX";
			else
				strTag = "X";
		}
		else
		{
			if (p.uS>15000)
				strTag = "-----------------BIG-SPACE------------------";
			else if (p.uS>m_nMaxSig)
				strTag = "------------";
			else if (p.uS>m_nMaxSht)
				strTag = "XXXXXXXXXXXX";
			else
				strTag = "X";
		}
		CString str;
		str.Format("%1s %8d uS %s\n",p.bPulse?"P":"S",p.uS,(LPCTSTR)strTag);
		pf->WriteString(str);
	}
	pf->WriteString("\n");

	BOOL cLong[] = {0,0};
	BOOL cShort[] = {0,0};
	BOOL cTotal[] = {0,0};
	int nTotLong[] = {0,0};
	int nTotShort[] = {0,0};
	int nMinLong[] = {INT_MAX,INT_MAX};
	int nMinShort[] = {INT_MAX,INT_MAX};
	int nMaxLong[] = {INT_MIN,INT_MIN};
	int nMaxShort[] = {INT_MIN,INT_MIN};
	for (i = 0; i<m_rPulse.GetSize(); i++)
	{
		Pulse& p = m_rPulse[i];

		// count up long and short pulses and spaces
		if (p.uS>m_nMaxSig)
			;
		else if (p.uS>m_nMaxSht)
		{
			cLong[p.bPulse]++;
			nTotLong[p.bPulse] += p.uS;
			if (p.uS<nMinLong[p.bPulse])
				nMinLong[p.bPulse] = p.uS;
			if (p.uS>nMaxLong[p.bPulse])
				nMaxLong[p.bPulse] = p.uS;
		}
		else
		{
			cShort[p.bPulse]++;
			nTotShort[p.bPulse] += p.uS;
			if (p.uS<nMinShort[p.bPulse])
				nMinShort[p.bPulse] = p.uS;
			if (p.uS>nMaxShort[p.bPulse])
				nMaxShort[p.bPulse] = p.uS;
		}
	}
	cTotal[TRUE] = cLong[TRUE]+cShort[TRUE];
	cTotal[FALSE] = cLong[FALSE]+cShort[FALSE];

	CString strMax;
	if (cTotal[TRUE]+cTotal[FALSE]==0)
	{
		pf->WriteString("NO SIGNAL DETECTED\n");
	}
	else
	{
		pf->WriteString("Summary of long and short durations (by pulse and space)\n");
		CString str;
		if (cLong[TRUE])
		str.Format("long pulses: %.0f%%, %d-%d -> %.0f\n",(double)cLong[TRUE]*100/cTotal[TRUE],
			nMinLong[TRUE],nMaxLong[TRUE],(double)nTotLong[TRUE]/cLong[TRUE]);
		else str.Format("long pulses: (none)\n");
		pf->WriteString(str);
		if (cShort[TRUE])
		str.Format("shrt pulses: %.0f%%, %d-%d -> %.0f\n",(double)cShort[TRUE]*100/cTotal[TRUE],
			nMinShort[TRUE],nMaxShort[TRUE],(double)nTotShort[TRUE]/cShort[TRUE]);
		else str.Format("shrt pulses: (none)\n");
		pf->WriteString(str);
		if (cLong[FALSE])
		str.Format("long spaces: %.0f%%, %d-%d -> %.0f\n",(double)cLong[FALSE]*100/cTotal[FALSE],
			nMinLong[FALSE],nMaxLong[FALSE],(double)nTotLong[FALSE]/cLong[FALSE]);
		else str.Format("long spaces: (none)\n");
		pf->WriteString(str);
		if (cShort[FALSE])
		str.Format("shrt spaces: %.0f%%, %d-%d -> %.0f\n",(double)cShort[FALSE]*100/cTotal[FALSE],
			nMinShort[FALSE],nMaxShort[FALSE],(double)nTotShort[FALSE]/cShort[FALSE]);
		else str.Format("shrt spaces: (none)\n");
		pf->WriteString(str);

		BOOL bLongPulses = (double)cLong[TRUE]*100/cTotal[TRUE] >= 10;
		BOOL bLongSpaces = (double)cLong[FALSE]*100/cTotal[FALSE] >= 10;
		pf->WriteString("Encoding: ");
		if (bLongPulses&&bLongSpaces)
		{
			m_bShift = TRUE;
			pf->WriteString("SHIFT");
		}
		else if (bLongPulses)
		{
			m_bPulse = TRUE;
			pf->WriteString("PULSE");
		}
		else if (bLongSpaces)
		{
			m_bSpace = TRUE;
			pf->WriteString("SPACE");
		}
		else
			pf->WriteString("UNKNOWN");
		pf->WriteString("\n");



		pf->WriteString("Commands received (results of analysis of received signals):\n");
		CMap<MyString,MyString&,int,int> mapCmd;
		if (m_bShift)
		{
			CArray<Pulse,Pulse&> rPulseExpanded;
			for (i = 0; i<m_rPulse.GetSize(); i++)
			{
				Pulse& p = m_rPulse[i];
				if ((p.uS>m_nMaxSig)||(p.uS<=m_nMaxSht))
				{
					rPulseExpanded.Add(p);
				}
				else
				{
					Pulse pExpanded;
					pExpanded.uS = p.uS/2;
					pExpanded.bPulse = p.bPulse;
					rPulseExpanded.Add(pExpanded);
					rPulseExpanded.Add(pExpanded);
				}
			}
			PrintCommands(rPulseExpanded,mapCmd);
		}
		else
			PrintCommands(m_rPulse,mapCmd);
		pf->WriteString("\n");

		pf->WriteString("Summary of commands received:\n");
		PrintCommandSummary(mapCmd,pf);
		int n(0);
		POSITION pos = mapCmd.GetStartPosition();
		int cMax(0);
		while (pos)
		{
			MyString str;
			int c;
			mapCmd.GetNextAssoc(pos,str,c);
			if (c>cMax)
			{
				cMax = c;
				strMax = str;
			}
		}
	}
	pf->WriteString("--------------------------\n");
	return strMax;
}

CString BinStr(unsigned int nCommand, int cBits)
{
	CString s;
	for (int i(0); i<cBits; i++)
	{
		if (nCommand&1)
			s = "1"+s;
		else
			s = "0"+s;
		nCommand = nCommand>>1;
	}
	return s;
}

CString Mirror(const CString& str)
{
	CString strMirror;
	int n = str.GetLength();
	for (int i(0); i<n; i++)
		strMirror += str[n-i-1];
	return strMirror;
}

void AddToMap(CMap<int,int,int,int>& map, int uS)
{
	int nCountFromMap(0);
	if (!map.Lookup(uS,nCountFromMap))
		map[uS] = 0;
	map[uS] = nCountFromMap+1;
}

static int __cdecl compare(const void *elem1, const void *elem2 )
{
	int a = ((Dur*)elem1)->uS;
	int b = ((Dur*)elem2)->uS;
	if (a<b)
		return -1;
	if (a>b)
		return 1;
	return 0;
}

void FilterMap(CMap<int,int,int,int>& map)
{
	return;
	CArray<int,int> rRemove;
	POSITION pos = map.GetStartPosition();
	while (pos)
	{
		int uS, c;
		map.GetNextAssoc(pos,uS,c);
		if (c<=2)
			rRemove.Add(uS);
	}
	for (int i(0); i<rRemove.GetSize(); i++)
		map.RemoveKey(rRemove[i]);
}

void SortMap(CMap<int,int,int,int>& map, Dur rDur[])
{
	int n(0);
	POSITION pos = map.GetStartPosition();
	while (pos)
	{
		int uS, c;
		map.GetNextAssoc(pos,uS,c);
		rDur[n].uS = uS;
		rDur[n].c = c;
		n++;
	}
	qsort(rDur,n,sizeof(Dur),compare);
}

void ShowDistribution(CMap<int,int,int,int>& map, CStdioFile* pf, CString& strCutoffs)
{
	FilterMap(map);
	int n = map.GetCount();
	Dur* pDur = new Dur[n];
	SortMap(map,pDur);
	int uSPrev(0);
	for (int i(0); i<n; i++)
	{
		int nJump = pDur[i].uS-uSPrev;
		int nJumpPct = (int)((double)nJump*100/uSPrev+.5);
		CString str;
		if (uSPrev && nJumpPct>40)
		{
			int nCutoff = (int)((pDur[i].uS+uSPrev+.5)/2);
			str.Format("  %8d uS: %8d <--- %d%% jump, possible cutoff @ %d uS\n",
				pDur[i].uS,pDur[i].c,
				nJumpPct,
				nCutoff);
			CString strCutoff;
			strCutoff.Format("%8d (%8d -%8d)\n",nCutoff,uSPrev,pDur[i].uS);
			strCutoffs += strCutoff;
		}
		else
		{
			str.Format("  %8d uS: %8d\n",
				pDur[i].uS,pDur[i].c);
		}
		pf->WriteString(str);
		uSPrev = pDur[i].uS;
	}
	delete [] pDur;
}

void CParser::PrintCommands(CArray<CParser::Pulse,CParser::Pulse&>& rPulse, CMap<MyString,MyString&,int,int>& mapCmd)
{
	unsigned int nCommand(-1);
	int cBits(0);
	for (int i(0); i<rPulse.GetSize(); i++)
	{
		Pulse& p = rPulse[i];
		if (p.uS>m_nMaxSig)
		{
			NextCommand(nCommand,cBits,pf,mapCmd);
		}
		else
		{
			if (m_bPulse||m_bSpace)
			{
				if ((p.bPulse==m_bPulse)||(!p.bPulse&&m_bSpace))
				{
					cBits++;
					AddBit(nCommand,p.uS>m_nMaxSht);
				}
			}
			else // shift
			{
				int iNext = i+1;
				if ((iNext<rPulse.GetSize() &&
					rPulse[iNext].uS<=m_nMaxSig))
				{
					cBits++;
					AddBit(nCommand,rPulse[i = iNext].bPulse);
				}
			}
		}
	}
	NextCommand(nCommand,cBits,pf,mapCmd);
}

void PrintCommandSummary(CMap<MyString,MyString&,int,int>& map, CStdioFile* pf)
{
	int n(0);
	POSITION pos = map.GetStartPosition();
	while (pos)
	{
		MyString str;
		int c;
		map.GetNextAssoc(pos,str,c);
		CString strm;
		strm.Format("%8d %s\n",c,(LPCTSTR)str);
		pf->WriteString(strm);
	}
}

void AddToCmdMap(CMap<MyString,MyString&,int,int>& map, MyString& strCmd)
{
	int nCountFromMap(0);
	if (!map.Lookup(strCmd,nCountFromMap))
		map[strCmd] = 0;
	map[strCmd] = nCountFromMap+1;
}

void AddBit(unsigned int& nCommand, int nThisBit)
{
	if (nCommand==-1)
		nCommand = nThisBit;
	else
		nCommand = (nCommand<<1)|nThisBit;
}

void NextCommand(unsigned int& nCommand, int& cBits, CStdioFile* pf, CMap<MyString,MyString&,int,int>& mapCmd)
{
	if (nCommand!=-1)
	{
		CString strBits = BinStr(nCommand,cBits);
		MyString strMy = (MyString)strBits;
		AddToCmdMap(mapCmd,strMy);
		pf->WriteString(strBits);
		pf->WriteString("\n");
		cBits = 0;
		nCommand = -1;
	}
}
