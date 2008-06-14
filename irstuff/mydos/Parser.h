#pragma once

class CParser
{
public:
	struct Pulse
	{
		bool bPulse;
		int uS;
		Pulse(bool b = false, int n = 0):
			bPulse(b), uS(n) { }
	};

	CParser();
	virtual ~CParser();

	void Add(bool bPulse, int uS);
	string Parse();
	void SetParams(int nMaxSig, int nMaxSht)
	{
		m_nMaxSig = nMaxSig;
		m_nMaxSht = nMaxSht;
	}



private:
//	CStdioFile* pf;
	int m_cPulse;
	vector<Pulse> m_rPulse;
//	CArray<Pulse,Pulse&> m_rPulse;
	bool m_bShift;
	bool m_bPulse;
	bool m_bSpace;
	int m_nMaxSig;
	int m_nMaxSht;
	void PrintCommands(vector<Pulse>& rPulse, map<string,int>& mapCmd);
};
