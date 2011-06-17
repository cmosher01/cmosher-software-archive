#if !defined(AFX_PRODOSDATE_H__2C420698_26C6_11D3_9A03_204C4F4F5020__INCLUDED_)
#define AFX_PRODOSDATE_H__2C420698_26C6_11D3_9A03_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// ProdosDate.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CProdosDate

class CProdosDate
{
// Construction
public:
	CProdosDate();
	virtual ~CProdosDate();

// Attributes
public:
	long m_nProdosFormat;
	int m_nYear;
	int m_nMonth;
	int m_nDay;
	int m_nHour;
	int m_nMinute;
	CString GetFormatted() const;

// Operations
public:
	void Set(long nProdosDateTime);

// Implementation
public:
};

/////////////////////////////////////////////////////////////////////////////

#endif // !defined(AFX_PRODOSDATE_H__2C420698_26C6_11D3_9A03_204C4F4F5020__INCLUDED_)
