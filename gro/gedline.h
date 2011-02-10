class CGedtreeDoc;

class CGedLine
{
public:
	CGedLine(CGedtreeDoc* pDoc, const wxString& strLine);
	CGedtreeDoc* m_pDoc;
	wxString m_strID;
	wxString m_strTok;
	wxString m_strVal;
	wxString m_strValAsID;
	int m_nVal;
	int m_nLev;

	void ParseGedLine(const wxString& strLine);
	void Calc();
	void CalcID();
	wxString GetGedLine();
	int Level() { return m_nLev; }

	enum
	{
		TRIM_LEFT  = 1<<0,
		TRIM_RIGHT = 1<<1,
		COLLAPSE   = 1<<2,
		UPCASE     = 1<<3,
		DOWNCASE   = 1<<4,
		MIXCASE    = 1<<5
	};
	wxString GetCleanValue(int nFlags);
	BOOL IsChildOf(const wxString& strTok, const wxString& strID);
};

const wxChar cDelim = _T(' ');
const wxChar cID = _T('@');
