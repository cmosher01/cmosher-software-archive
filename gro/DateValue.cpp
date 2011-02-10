#include "stdafx.h"
#include "datevalue.h"
#include "gedline.h"
#include "util.h"

static const int cMonth(12);
static wxChar* rsMonth[cMonth] =
{
	_T("JAN"),_T("FEB"),_T("MAR"),_T("APR"),_T("MAY"),_T("JUN"),
	_T("JUL"),_T("AUG"),_T("SEP"),_T("OCT"),_T("NOV"),_T("DEC")
};

CDateValue::CDateValue() :
	m_type(NODATE)
{
}

CDateValue::~CDateValue()
{
}

void CDateValue::Set(const wxString& strGedcom)
{
	// assume strGedcom is clean (trimleft,trimright,collapse,upcase)
	wxString strKey, strRest;
	BOOL bOK;
	strRest = strGedcom;

do_next:

	strKey = NextWord(strRest);
	if (strKey.Right(1)==_T("."))
		strKey = strKey.Left(strKey.Length()-1);

	bOK = TRUE;

	if (strKey==_T("FROM"))
	{
		m_type = PERIOD;
		bOK = ParseDate(m_d,strRest);
		goto do_next;
	}
	else if (strKey==_T("TO"))
	{
		m_type = PERIOD;
		bOK = ParseDate(m_dOther,strRest);
	}
	else if (strKey==_T("AFT") || strKey==_T("AFTER"))
	{
		m_type = RANGE;
		bOK = ParseDate(m_d,strRest);
	}
	else if (strKey==_T("BEF") || strKey==_T("BEFORE"))
	{
		m_type = RANGE;
		bOK = ParseDate(m_dOther,strRest);
	}
	else if (strKey==_T("BET") || strKey==_T("BETWEEN"))
	{
		m_type = RANGE;
		bOK = ParseDate(m_d,strRest);
		goto do_next;
	}
	else if (strKey==_T("AND") || strKey==_T("-"))
	{
		m_type = RANGE;
		bOK = ParseDate(m_dOther,strRest);
	}
	else if (strKey==_T("ABT") || strKey==_T("ABOUT"))
	{
		m_type = ABOUT;
		bOK = ParseDate(m_d,strRest);
	}
	else if (strKey==_T("CAL"))
	{
		m_type = CALCULATED;
		bOK = ParseDate(m_d,strRest);
	}
	else if (strKey==_T("EST"))
	{
		m_type = ESTIMATED;
		bOK = ParseDate(m_d,strRest);
	}
	else if (strKey==_T("INT"))
	{
		m_type = INTERPRETED;
		bOK = ParseDate(m_d,strRest);
		goto do_next;
	}
	else if (strKey.Left(1)==_T("("))
	{
		if (m_type != INTERPRETED)
			m_type = PHRASE;
		m_strPhrase = strKey.Mid(1);
		if (!strRest.IsEmpty())
			m_strPhrase += wxString(cDelim)+strRest;
		if (m_strPhrase.Right(1)==_T(")"))
			m_strPhrase = m_strPhrase.Left(m_strPhrase.Length()-1);
	}
	else
	{
		if (m_type != PERIOD || !strRest.IsEmpty())
		{
			strRest = strGedcom;
			m_type = DATE;
			bOK = ParseDate(m_d,strRest);
		}
	}

	if (!bOK)
	{
		m_type = PHRASE;
		m_strPhrase = strGedcom;
	}

	CalcDisplay();

/*
	wxString strOutVal = Get();

	if (strOutVal!=strGedcom)
	{
		int nYear, nMonth, nDay;
		int nYear2, nMonth2, nDay2;
		m_d.Get(&nYear,&nMonth,&nDay);
		m_dOther.Get(&nYear2,&nMonth2,&nDay2);

		wxString sMsg;
		sMsg.Format(
			_T(_T("%s --> %d %4.4d.%2.2d.%2.2d %4.4d.%2.2d.%2.2d \")%s\_T(" --> \")%s\_T("\n")),
			(LPCTSTR)strGedcom,
			m_type,
			nYear,nMonth,nDay,
			nYear2,nMonth2,nDay2,
			(LPCTSTR)m_strPhrase,
			(LPCTSTR)strOutVal);
		OutputDebugString(sMsg);
	}
*/
}

BOOL CDateValue::ParseDate(CDate& date, wxString& strRest)
{
	wxString s(strRest);

	BOOL bGregorian(TRUE);
	int n;

	if (s.Left(3)==_T("@#D"))
	{
		wxString strCalendar = s.Mid(3);

		n = strCalendar.Find('@');
		if (n==-1) return FALSE;

		s = strCalendar.Mid(n+1);
		s.Trim(false);

		strCalendar = strCalendar.Left(n);
		strCalendar.Trim(false);
		strCalendar.Trim();

		if (strCalendar==_T("GREGORIAN"))
			wxASSERT(bGregorian);
		else if (strCalendar==_T("JULIAN"))
			bGregorian = FALSE;
		else
			return FALSE;
	}
//NEED TO PARSE [B.C.] after year
	// s is now of the form: [[DAY ]MONTH ]YEAR[/year][ B.C.][ rest]
	strRest = s;

	int nYear(0), nMonth(0), nDay(0);

	wxString strItem1 = NextWord(strRest);
	nMonth = Month(strItem1);
	if (nMonth)
	{
		// MONTH YEAR[/year][ B.C.][ rest]
		//       ^
		wxString strItem2 = NextWord(strRest);
		// MONTH YEAR[/year][ B.C.][ rest]
		//                   ^
		wxString strTemp(strRest); // look ahead for B.C.
		wxString strItem3 = NextWord(strTemp);
		// MONTH YEAR[/year][ B.C.][ rest]
		//                          ^temp
		if (strItem3==_T("B.C."))
		{
			nYear = -ParseYear(strItem2);
			strRest = strTemp;
		}
		else
			nYear = ParseYear(strItem2);
	}
	else
	{
		// DAY MONTH YEAR[/year][ B.C.][ rest]
		//     ^
		//---or---
		// YEAR[/year][ B.C.][ rest]
		//             ^
		wxString strTemp(strRest); // look ahead for month
		wxString strItem2 = NextWord(strTemp);
		nMonth = Month(strItem2);
		if (nMonth)
		{
			// DAY MONTH YEAR[/year][ B.C.][ rest]
			//           ^temp
			nDay = _ttoi(strItem1);
			wxString strItem3 = NextWord(strTemp);
			// DAY MONTH YEAR[/year][ B.C.][ rest]
			//                       ^temp
			strRest = strTemp; // found month when looking ahead

			wxString strItem4 = NextWord(strTemp);
			if (strItem4==_T("B.C."))
			{
				nYear = -ParseYear(strItem3);
				strRest = strTemp;
			}
			else
				nYear = ParseYear(strItem3);
		}
		else
		{
			// YEAR[/year][ B.C.][ rest]
			//             ^
			wxString strTemp(strRest); // look ahead for B.C.
			wxString strItem2 = NextWord(strTemp);
			if (strItem2==_T("B.C."))
			{
				nYear = -ParseYear(strItem1);
				strRest = strTemp;
			}
			else
				nYear = ParseYear(strItem1);
		}
	}

	if (bGregorian)
		date.Set(nYear,nMonth,nDay);
	else
		date.SetJulian(nYear,nMonth,nDay);

	return date;
}

int CDateValue::ParseYear(const wxString& str)
{
	// str is of the form: y[/y2]
	// where y and y2 are strings of digits

	int n = str.Find('/');
	if (n==-1) return _ttoi(str);

	return _ttoi(str.Left(n))+1;
}

int CDateValue::Month(const wxString& str)
{
	BOOL bFound(FALSE);
	int nMonth(0);

	for (int i(0); !bFound && i<cMonth; i++)
	{
		if (str==rsMonth[i])
		{
			bFound = TRUE;
			nMonth = i+1;
		}
	}

	return nMonth;
}

wxString CDateValue::NextWord(wxString& strLine)
{
	wxString strWord;
	int i = strLine.Find(cDelim);
	if (i==-1)
	{
		strWord = strLine;
		strLine.Empty();
	}
	else
	{
		strWord = strLine.Left(i);
		strLine = strLine.Mid(i+1);
	}
	return strWord;
}

wxString CDateValue::Get()
{
	wxString s;
	switch (m_type)
	{
		case DATE:
			s = FormatDate(m_d);
		break;
		case PERIOD:
			if (m_d)
				s += _T("FROM ")+FormatDate(m_d);
			if (m_d&&m_dOther) s += _T(" ");
			if (m_dOther)
				s += _T("TO ")+FormatDate(m_dOther);
		break;
		case RANGE:
			if (m_d&&m_dOther)
				s += _T("BET ")+FormatDate(m_d)+_T(" AND ")+FormatDate(m_dOther);
			else if (m_d)
				s += _T("AFT ")+FormatDate(m_d);
			else if (m_dOther)
				s += _T("BEF ")+FormatDate(m_dOther);
		break;
		case ABOUT:
			s = _T("ABT ")+FormatDate(m_d);
		break;
		case CALCULATED:
			s = _T("CAL ")+FormatDate(m_d);
		break;
		case ESTIMATED:
			s = _T("EST ")+FormatDate(m_d);
		break;
		case PHRASE:
			s = _T("(")+m_strPhrase+_T(")");
		break;
		case INTERPRETED:
			s = _T("INT ")+FormatDate(m_d)+_T(" (")+m_strPhrase+_T(")");
		break;
		case NODATE:
			wxASSERT(s.IsEmpty());
		break;
		default:
			wxASSERT(FALSE); // bad m_type value
	}
	return s;
}

wxString CDateValue::FormatDate(const CDate& d)
{
	wxString s;

	int nYear, nMonth, nDay;
	if (d.IsJulian())
	{
		d.GetJulian(&nYear,&nMonth,&nDay);
		s += _T("@#DJULIAN@ ");
	}
	else
		d.Get(&nYear,&nMonth,&nDay);

	if (nDay)
		s += CUtil::str(nDay)+_T(" ");
	if (nMonth)
		s += wxString(rsMonth[nMonth-1])+_T(" ");

	if (nYear>0)
	{
		s += CUtil::str(nYear);
	}
	else if (nYear<0)
	{
		s += CUtil::str(-nYear)+_T(" B.C.");
	}

	return s;
}

wxString CDateValue::GetDisplayString(DWORD dwFlags) const
{
	wxString s;

	switch (m_type)
	{
		case DATE:
			s = m_d.Display(dwFlags);
		break;
		case PERIOD:
			wxASSERT(s.IsEmpty());
			if (m_d)
				s += m_d.Display(dwFlags);
			else
				s += _T("?");
			s += _T("-");
			if (m_dOther)
				s += m_dOther.Display(dwFlags);
			else
				s += _T("?");
/*
			if (m_d&&m_dOther)
			{
				int d1 = m_d.GetJD();
				int d2 = m_dOther.GetJD();
				if (d1 && d2 && d1<d2)
				{
					int dif = d2-d1;
					if (dif==1)
						s += _T(" (1 day)");
					else
						s += _T(" (")+CUtil::str(dif)+_T(" days)");
				}
			}
*/
		break;
		case RANGE:
			wxASSERT(s.IsEmpty());
			if (m_d&&m_dOther)
				s += _T("btw ")+m_d.Display(dwFlags)+_T(" & ")+m_dOther.Display(dwFlags);
			else if (m_d)
				s += _T("aft ")+m_d.Display(dwFlags);
			else if (m_dOther)
				s += _T("bef ")+m_dOther.Display(dwFlags);
		break;
		case ABOUT:
			s = _T("c. ")+m_d.Display(dwFlags);
		break;
		case CALCULATED:
			s = _T("calc ")+m_d.Display(dwFlags);
		break;
		case ESTIMATED:
			s = _T("estd ")+m_d.Display(dwFlags);
		break;
		case PHRASE:
			s = _T("(")+m_strPhrase+_T(")");
		break;
		case INTERPRETED:
			s = _T("interpreted as ")+m_d.Display(dwFlags)+_T(" from \"")+m_strPhrase+_T("\"");
		break;
		case NODATE:
			wxASSERT(s.IsEmpty());
		break;
		default:
			wxASSERT(FALSE); // bad m_type value
	}

	return s;
}

wxString CDateValue::GetSortString() const
{
	wxString s;

	switch (m_type)
	{
		case DATE:
			s = m_d.Sort();
		break;
		case PERIOD:
			wxASSERT(s.IsEmpty());
			if (m_d)
				s += m_d.Sort();
			else
				s += _T("?");
			s += _T("-");
			if (m_dOther)
				s += m_dOther.Sort();
			else
				s += _T("?");
		break;
		case RANGE:
			wxASSERT(s.IsEmpty());
			if (m_d&&m_dOther)
				s += m_d.Sort()+_T("/")+m_dOther.Sort();
			else if (m_d)
				s += m_d.Sort()+_T("/(after)");
			else if (m_dOther)
				s += m_dOther.Sort()+_T("/(before)");
		break;
		case ABOUT:
			s = m_d.Sort()+_T(" (circa)");
		break;
		case CALCULATED:
			s = m_d.Sort()+_T(" (calc.)");
		break;
		case ESTIMATED:
			s = m_d.Sort()+_T(" (est.)");
		break;
		case PHRASE:
			s = _T("(")+m_strPhrase+_T(")");
		break;
		case INTERPRETED:
			s = m_d.Sort()+_T(" (")+m_strPhrase+_T(")");
		break;
		case NODATE:
			wxASSERT(s.IsEmpty());
		break;
		default:
			wxASSERT(FALSE); // bad m_type value
	}

	return s;
}

void CDateValue::CalcDisplay()
{
	m_strDisplayShort = GetDisplayString(DATE_SHORTDATE);
	m_strDisplayLong = GetDisplayString(DATE_LONGDATE);
	m_strSort = GetSortString();
}

void CDateValue::Set(Type type, CDate d, CDate d2, const wxString& strPhrase)
{
	m_type = type;
	m_d = d;
	m_dOther = d2;
	m_strPhrase = strPhrase;
	CalcDisplay();
}

void CDateValue::Get(Type& type, CDate& d, CDate& d2, wxString& strPhrase)
{
	type = m_type;
	d = m_d;
	d2 = m_dOther;
	strPhrase = m_strPhrase;
}

wxString CDateValue::Display(DWORD dwFlags) const
{
	if (dwFlags&DATE_SHORTDATE)
		return m_strDisplayShort;
	else
		return m_strDisplayLong;
}

int CDateValue::GetSimpleYear()
{
	int nYear1 = m_d.GetSimpleYear();
	int nYear2 = m_dOther.GetSimpleYear();
	if (nYear1==0)
		return nYear2;
	if (nYear2==0)
		return nYear1;
	return std::min(nYear1,nYear2);
}

int CDateValue::GetSimpleYMD()
{
	int nYMD1 = m_d.GetSimpleYMD();
	int nYMD2 = m_dOther.GetSimpleYMD();
	if (nYMD1==0)
		return nYMD2;
	if (nYMD2==0)
		return nYMD1;
	return std::min(nYMD1,nYMD2);
}
int CDateValue::GetSimpleMonthCount()
{
	int nM1 = m_d.GetSimpleMonthCount();
	int nM2 = m_dOther.GetSimpleMonthCount();
	if (nM1==0)
		return nM2;
	if (nM2==0)
		return nM1;
	return std::min(nM1,nM2);
}
