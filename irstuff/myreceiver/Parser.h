#pragma once
class MyString : public CString
{
public:
	MyString() { }
	MyString(CString s) : CString(s) { }
	operator DWORD() { return 1; }
};

class CParser
{
public:
	struct Pulse
	{
		BOOL bPulse;
		int uS;
		Pulse(BOOL b = FALSE, int n = 0):
			bPulse(b), uS(n) { }
	};

	CParser();
	virtual ~CParser();

	void Add(BOOL bPulse, int uS);
	CString Parse();
	void SetParams(int nMaxSig, int nMaxSht, CStdioFile& f)
	{
		m_nMaxSig = nMaxSig;
		m_nMaxSht = nMaxSht;
		pf = &f;
	}



private:
	CStdioFile* pf;
	int m_cPulse;
	CArray<Pulse,Pulse&> m_rPulse;
	BOOL m_bShift;
	BOOL m_bPulse;
	BOOL m_bSpace;
	int m_nMaxSig;
	int m_nMaxSht;
	void PrintCommands(CArray<Pulse,Pulse&>& rPulse, CMap<MyString,MyString&,int,int>& mapCmd);
};
