#include "stdafx.h"
#include "Parser.h"

struct Dur
{
	int uS;
	int c;
};

static string BinStr(unsigned int nCommand, int cBits = 32);
static string Mirror(const string& str);
static void AddToMap(map<int,int>& mp, int uS);
static void AddToCmdMap(map<string,int>& mp, string& strCmd);
static void SortMap(map<int,int>& mp, Dur rDur[]);
static void ShowDistribution(map<int,int>& mp,  string& strCutoffs);
static void FilterMap(map<int,int>& mp);
static void AddBit(unsigned int& nCommand, int nThisBit);
static void NextCommand(unsigned int& nCommand, int& cBits, map<string,int>& mapCmd);
static void PrintCommandSummary(map<string,int>& mp);

CParser::CParser():
	m_cPulse(0),
	m_bShift(false),
	m_bPulse(false),
	m_bSpace(false)
{
}

CParser::~CParser()
{
}

void CParser::Add(bool bPulse, int uS)
{
	Pulse p(bPulse,uS);
	m_rPulse.push_back(p);
}

string CParser::Parse()
{
	int i;

	map<int,int> /*uS->count*/ mapPulse, mapSpace;
	for (i = 0; i<m_rPulse.size(); i++)
	{
		Pulse& p = m_rPulse[i];
		AddToMap(p.bPulse?mapPulse:mapSpace,p.uS);
	}
	cout << "Full distribution of durations:" << endl;
	cout << "  pulses:" << endl;
	string strCutoffsPulse;
	ShowDistribution(mapPulse,strCutoffsPulse);
	cout << "  spaces:" << endl;
	string strCutoffsSpace;
	ShowDistribution(mapSpace,strCutoffsSpace);
	cout << endl;

	cout << "Summary of cutoffs for durations:" << endl;
	cout << "  pulses:" << endl;
	cout << strCutoffsPulse;
	cout << "  spaces:" << endl;
	cout << strCutoffsSpace;
	cout << endl;

	cout << "Full dump of received pulses and spaces for entire session:" << endl;
	for (i = 0; i<m_rPulse.size(); i++)
	{
		Pulse& p = m_rPulse[i];
		string strTag;
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
		cout << (p.bPulse?"P ":"S ") << p.uS << " " << strTag << endl;
	}
	cout << endl;

	bool cLong[] = {0,0};
	bool cShort[] = {0,0};
	bool cTotal[] = {0,0};
	int nTotLong[] = {0,0};
	int nTotShort[] = {0,0};
	int nMinLong[] = {INT_MAX,INT_MAX};
	int nMinShort[] = {INT_MAX,INT_MAX};
	int nMaxLong[] = {INT_MIN,INT_MIN};
	int nMaxShort[] = {INT_MIN,INT_MIN};
	for (i = 0; i<m_rPulse.size(); i++)
	{
		Pulse& p = m_rPulse[i];

		// count up long and short pulses and spaces
		if (p.uS>m_nMaxSig)
			;
		else if (p.uS>m_nMaxSht)
		{
			cLong[p.bPulse?1:0]++;
			nTotLong[p.bPulse] += p.uS;
			if (p.uS<nMinLong[p.bPulse])
				nMinLong[p.bPulse] = p.uS;
			if (p.uS>nMaxLong[p.bPulse])
				nMaxLong[p.bPulse] = p.uS;
		}
		else
		{
			cShort[p.bPulse?1:0]++;
			nTotShort[p.bPulse] += p.uS;
			if (p.uS<nMinShort[p.bPulse])
				nMinShort[p.bPulse] = p.uS;
			if (p.uS>nMaxShort[p.bPulse])
				nMaxShort[p.bPulse] = p.uS;
		}
	}
	cTotal[1] = ((cLong[1]?1:0)+(cShort[1]?1:0)) != 0;
	cTotal[0] = ((cLong[0]?1:0)+(cShort[0]?1:0)) != 0;

	string strMax;
	if (cTotal[1]+cTotal[0]==0)
	{
		cout << "NO SIGNAL DETECTED" << endl;
	}
	else
	{
		cout << "Summary of long and short durations (by pulse and space)" << endl;
		string str;
		if (cLong[1])
			cout << "long pulses: " << (double)(cLong[1]?1:0)*100/(cTotal[1]?1:0) << ", " << nMinLong[1] << "-" << nMaxLong[1] << " -> " << (double)nTotLong[1]/(cLong[1]?1:0) << endl;
		else
			cout << "long pulses: (none)" << endl;

		if (cShort[1])
			cout << "shrt pulses: " << (double)(cShort[1]?1:0)*100/(cTotal[1]?1:0) << ", " << nMinShort[1] << "-" << nMaxShort[1] << " -> " << (double)nTotShort[1]/(cShort[1]?1:0) << endl;
		else
			cout << "shrt pulses: (none)" << endl;

		if (cLong[0])
			cout << "long spaces: " << (double)(cLong[0]?1:0)*100/(cTotal[0]?1:0) << ", " << nMinLong[0] << "-" << nMaxLong[0] << " -> " << (double)nTotLong[0]/(cLong[0]?1:0) << endl;
		else
			cout << "long spaces: (none)" << endl;

		if (cShort[0])
			cout << "shrt spaces: " << (double)(cShort[0]?1:0)*100/(cTotal[0]?1:0) << ", " << nMinShort[0] << "-" << nMaxShort[0] << " -> " << (double)nTotShort[0]/(cShort[0]?1:0) << endl;
		else
			cout << "shrt spaces: (none)" << endl;

		bool bLongPulses = (double)(cLong[1]?1:0)*100/(cTotal[1]?1:0) >= 10;
		bool bLongSpaces = (double)(cLong[0]?1:0)*100/(cTotal[0]?1:0) >= 10;
		cout << "Encoding: ";
		if (bLongPulses&&bLongSpaces)
		{
			m_bShift = true;
			cout << "SHIFT";
		}
		else if (bLongPulses)
		{
			m_bPulse = true;
			cout << "PULSE";
		}
		else if (bLongSpaces)
		{
			m_bSpace = true;
			cout << "SPACE";
		}
		else
			cout << "UNKNOWN";
		cout << endl;



		cout << "Commands received (results of analysis of received signals):" << endl;
		map<string,int> mapCmd;
		if (m_bShift)
		{
			vector<Pulse> rPulseExpanded;
			for (i = 0; i<m_rPulse.size(); i++)
			{
				Pulse& p = m_rPulse[i];
				if ((p.uS>m_nMaxSig)||(p.uS<=m_nMaxSht))
				{
					rPulseExpanded.push_back(p);
				}
				else
				{
					Pulse pExpanded;
					pExpanded.uS = p.uS/2;
					pExpanded.bPulse = p.bPulse;
					rPulseExpanded.push_back(pExpanded);
					rPulseExpanded.push_back(pExpanded);
				}
			}
			PrintCommands(rPulseExpanded,mapCmd);
		}
		else
			PrintCommands(m_rPulse,mapCmd);
		cout << endl;

		cout << "Summary of commands received:" << endl;
		PrintCommandSummary(mapCmd);
		int n(0);
		map<string,int>::const_iterator pos = mapCmd.begin();
		int cMax(0);
		while (pos != mapCmd.end())
		{
			string str = pos->first;
			int c = pos->second;
			if (c>cMax)
			{
				cMax = c;
				strMax = str;
			}
			++pos;
		}
	}
	cout << "--------------------------" << endl;
	return strMax;
}

string BinStr(unsigned int nCommand, int cBits)
{
	string s;
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

string Mirror(const string& str)
{
	string strMirror;
	int n = str.length();
	for (int i(0); i<n; i++)
		strMirror += str[n-i-1];
	return strMirror;
}

void AddToMap(map<int,int>& mp, int uS)
{
	mp[uS]++;
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

void FilterMap(map<int,int>& mp)
{
	return;
	vector<int> rRemove;
	map<int,int>::const_iterator pos = mp.begin();
	while (pos != mp.end())
	{
		int uS = pos->first;
		int c = pos->second;
		if (c<=2)
			rRemove.push_back(uS);
		++pos;
	}
	for (int i(0); i<rRemove.size(); i++)
		mp.erase(rRemove[i]);
}

void SortMap(map<int,int>& mp, Dur rDur[])
{
	int n(0);
	map<int,int>::const_iterator pos = mp.begin();
	while (pos != mp.end())
	{
		int uS = pos->first;
		int c = pos->second;
		rDur[n].uS = uS;
		rDur[n].c = c;
		n++;
		++pos;
	}
	qsort(rDur,n,sizeof(Dur),compare);
}

void ShowDistribution(map<int,int>& mp, string& strCutoffs)
{
	FilterMap(mp);
	int n = mp.size();
	Dur* pDur = new Dur[n];
	SortMap(mp,pDur);
	int uSPrev(0);
	for (int i(0); i<n; i++)
	{
		int nJump = pDur[i].uS-uSPrev;
		int nJumpPct = (int)((double)nJump*100/uSPrev+.5);
		string str;
		if (uSPrev && nJumpPct>40)
		{
			int nCutoff = (int)((pDur[i].uS+uSPrev+.5)/2);
			cout << "  " << pDur[i].uS << " uS: " << pDur[i].c << " <---- " << nJumpPct << "% jump; possible cutoof @ " << nCutoff << " uS" << endl;

			char rc[32];
			_itoa(nCutoff,rc,10);
			string strCutoff;
			strCutoff = rc;
			strCutoff += " (";
			_itoa(uSPrev,rc,10);
			strCutoff += rc;
			strCutoff += " - ";
			_itoa(pDur[i].uS,rc,10);
			strCutoff += rc;
			strCutoff += ")\n";
//			strCutoff.Format("%8d (%8d -%8d)\n",nCutoff,uSPrev,pDur[i].uS);
			strCutoffs += strCutoff;
		}
		else
		{
			cout << "  " << pDur[i].uS << " uS: " << pDur[i].c << endl;
		}
		uSPrev = pDur[i].uS;
	}
	delete [] pDur;
}

void CParser::PrintCommands(vector<CParser::Pulse>& rPulse, map<string,int>& mapCmd)
{
	unsigned int nCommand(-1);
	int cBits(0);
	for (int i(0); i<rPulse.size(); i++)
	{
		Pulse& p = rPulse[i];
		if (p.uS>m_nMaxSig)
		{
			NextCommand(nCommand,cBits,mapCmd);
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
				if ((iNext<rPulse.size() &&
					rPulse[iNext].uS<=m_nMaxSig))
				{
					cBits++;
					AddBit(nCommand,rPulse[i = iNext].bPulse);
				}
			}
		}
	}
	NextCommand(nCommand,cBits,mapCmd);
}

void PrintCommandSummary(map<string,int>& mp)
{
	int n(0);
	map<string,int>::const_iterator pos = mp.begin();
	while (pos != mp.end())
	{
		cout << pos->second << " " << pos->first << endl;
		++pos;
	}
}

void AddToCmdMap(map<string,int>& mp, string& strCmd)
{
	mp[strCmd]++;
}

void AddBit(unsigned int& nCommand, int nThisBit)
{
	if (nCommand==-1)
		nCommand = nThisBit;
	else
		nCommand = (nCommand<<1)|nThisBit;
}

void NextCommand(unsigned int& nCommand, int& cBits, map<string,int>& mapCmd)
{
	if (nCommand!=-1)
	{
		string strBits = BinStr(nCommand,cBits);
		string strMy = (string)strBits;
		AddToCmdMap(mapCmd,strMy);
		cout << strBits;
		cout << endl;
		cBits = 0;
		nCommand = -1;
	}
}
