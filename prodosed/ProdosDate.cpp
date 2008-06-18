// ProdosDate.cpp : implementation file
//

#include "stdafx.h"
#include "pdewin.h"
#include "ProdosDate.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CProdosDate

CProdosDate::CProdosDate():
	m_nProdosFormat(0),
	m_nYear(0),
	m_nMonth(0),
	m_nDay(0),
	m_nHour(0),
	m_nMinute(0)
{
}

CProdosDate::~CProdosDate()
{
}

void CProdosDate::Set(long nProdosDateTime)
{
	short nTime = HIWORD(nProdosDateTime);
	m_nHour = HIBYTE(nTime);
	m_nMinute = LOBYTE(nTime);

	short nDate = LOWORD(nProdosDateTime);
//	nDate = MAKEWORD(HIBYTE(nDate),LOBYTE(nDate));
	m_nYear = (nDate >> 9)  & 0x007F + 1900;
	m_nMonth = (nDate >> 5) & 0x000F;
	m_nDay = (nDate) & 0x001F;
}

CString CProdosDate::GetFormatted() const
{
	CString str;
	str.Format("%4.4d/%2.2d/%2.2d %2.2d:%2.2d",
		m_nYear,
		m_nMonth,
		m_nDay,
		m_nHour,
		m_nMinute);
	return str;
}
