#ifndef _datevalue_
#define _datevalue_

#include "date.h"

class CDateValue
{
public:
	enum Type
	{
		NODATE,
		DATE,
		PERIOD,
		RANGE,
		ABOUT,
		CALCULATED,
		ESTIMATED,
		PHRASE,
		INTERPRETED
	};

private:
	wxString m_strDisplayShort;
	wxString m_strDisplayLong;
	wxString m_strSort;

	Type m_type;

	CDate m_d;
	// "before" or "and" (for range) or "to" (for period)
	CDate m_dOther;

	wxString m_strPhrase;

	BOOL ParseDate(CDate& date, wxString& strRest);
	int ParseYear(const wxString& str);
	int Month(const wxString& str);
	wxString NextWord(wxString& strRest);
	wxString FormatDate(const CDate& d);
	void CalcDisplay();
	wxString GetDisplayString(DWORD dwFlags = DATE_LONGDATE) const;
	wxString GetSortString() const;

public:
	CDateValue();
	virtual ~CDateValue();

	void Set(const wxString& strGedcom);
	void Set(Type type, CDate d, CDate d2, const wxString& strPhrase);
	void Get(Type& type, CDate& d, CDate& d2, wxString& strPhrase);
	wxString Get();
	operator BOOL () { return m_type != NODATE; }
	BOOL IsValid() { return m_d.IsValid() && m_dOther.IsValid(); }

	wxString Display(DWORD dwFlags = DATE_LONGDATE) const;
	wxString Sort() { return m_strSort; }
	CDate GetExact() { wxASSERT(IsExact()); return m_d; }
	int GetSimpleYear();
	int GetSimpleYMD();
	int GetSimpleMonthCount();
	bool IsExact() { return m_type==DATE; }
	void SetExact(const CDate& d) { m_type = DATE; m_d = d; CalcDisplay(); }
};

#endif
