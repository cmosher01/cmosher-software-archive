// BirthCalc.cpp : implementation file
//

#include "stdafx.h"
#include "gedtree.h"
#include "BirthCalc.h"
#include "EditDate.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CBirthCalc dialog


CBirthCalc::CBirthCalc(wxWindow* pParent /*=NULL*/)
{
	//{{AFX_DATA_INIT(CBirthCalc)
	m_nAge = 0;
	m_nYear1 = 0;
	m_nYear2 = 0;
	m_nMonth = 0;
	m_nDay = 0;
	m_nYear3 = 1;
	m_strDateInput = _T("");
	m_strBirthDate = _T("");
	m_strCensusBirthDate = _T("");
	m_strCensusDateStatic = _T("");
	m_strYearOfAge = _T("");
	//}}AFX_DATA_INIT
}

void CBirthCalc::DoDataExchange(CDataExchange* pDX)
{
	//wxDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CBirthCalc)
	DDX_Radio(pDX, IDC_AGE1, m_nAge);
	DDX_Text(pDX, IDC_YEARS, m_nYear1);
	DDV_MinMaxInt(pDX, m_nYear1, 0, 2147483647);
	DDX_Text(pDX, IDC_YEARS2, m_nYear2);
	DDV_MinMaxInt(pDX, m_nYear2, 0, 2147483647);
	DDX_Text(pDX, IDC_MONTHS, m_nMonth);
	DDV_MinMaxInt(pDX, m_nMonth, 0, 2147483647);
	DDX_Text(pDX, IDC_DAYS, m_nDay);
	DDV_MinMaxInt(pDX, m_nDay, 0, 2147483647);
	DDX_Text(pDX, IDC_YEARS3, m_nYear3);
	DDV_MinMaxInt(pDX, m_nYear3, 1, 2147483647);
	DDX_Text(pDX, IDC_DATE_IN, m_strDateInput);
	DDX_Text(pDX, IDC_BIRTH, m_strBirthDate);
	DDX_Text(pDX, IDC_CENSUS_BIRTH, m_strCensusBirthDate);
	DDX_Text(pDX, IDC_CENSUS_LABEL, m_strCensusDateStatic);
	DDX_Text(pDX, IDC_YEAROFAGE, m_strYearOfAge);
	//}}AFX_DATA_MAP
	if (pDX->m_bSaveAndValidate)
	{
		m_strDateInput = m_dvDate.Display(DATE_SHORTDATE);
//		GetDlgItem(IDC_YEARS)->EnableWindow(m_nAge==0);
//		GetDlgItem(IDC_YEARS2)->EnableWindow(m_nAge==1);
//		GetDlgItem(IDC_MONTHS)->EnableWindow(m_nAge==1);
//		GetDlgItem(IDC_DAYS)->EnableWindow(m_nAge==1);
//		GetDlgItem(IDC_YEARS3)->EnableWindow(m_nAge==2);
		if (m_dvDate.IsExact())
		{
			CalcBirth(m_dvDate,m_dvDateBirth);

			m_strBirthDate = m_dvDateBirth.Display(DATE_SHORTDATE);

			int nYear, nMonth, nDay;
			CDate d(m_dvDate.GetExact());
			d.Get(&nYear,&nMonth,&nDay);
			d.SetHoliday(nYear,CDate::Census);
			if (d.GetJD())
			{
				m_dvDateCensus.Set(CDateValue::DATE,d,CDate(),_T(""));
				m_strCensusDateStatic = _T("Calculated birth date based on \"Census Day\" ");
				m_strCensusDateStatic += m_dvDateCensus.Display(DATE_SHORTDATE);

				CalcBirth(m_dvDateCensus,m_dvDateBirthCensus);
				m_strCensusBirthDate = m_dvDateBirthCensus.Display(DATE_SHORTDATE);
			}
			else
			{
				m_strCensusDateStatic = _T("");
				m_strCensusBirthDate = _T("");
			}
		}
		else
		{
			m_strBirthDate = _T("");
			m_strCensusDateStatic = _T("");
			m_strCensusBirthDate = _T("");
		}
		wxString sOrd(_T("th"));
		if (m_nYear3==1)
			sOrd = _T("st");
		else if (m_nYear3==2)
			sOrd = _T("nd");
		else if (m_nYear3==3)
			sOrd = _T("rd");
		m_strYearOfAge = sOrd+_T(" year of his/her age");
	}
}

void CBirthCalc::CalcBirth(CDateValue& dvInput, CDateValue& dvBirth)
{
	if (m_nAge==0 || m_nAge==2)
	{
		int nYear, nMonth, nDay;
		int nYearx, nMonthx, nDayx;
		dvInput.GetExact().Get(&nYear,&nMonth,&nDay);
		if (m_nAge==0)
			nYear -= m_nYear1;
		else
			nYear -= (m_nYear3-1);
		CDate d1, d2;
		d1.Set(nYear-1,nMonth,nDay);
		d1 += 1;
		d1.Get(&nYearx,&nMonthx,&nDayx);
		d1.Set(nYearx,nMonthx,nDayx);
		d2.Set(nYear,nMonth,nDay);
		dvBirth.Set(CDateValue::RANGE,d1,d2,_T(""));
	}
	else
	{
		int nYear, nMonth, nDay;

		CDate d1(dvInput.GetExact());
		d1 -= m_nDay;
		d1.Get(&nYear,&nMonth,&nDay);

		nMonth -= m_nMonth;
		while (nMonth <= 0)
		{
			nYear--;
			nMonth += 12;
		}
		bool bBad(false);
		while (nDay > CDate::MonthSize(nYear,nMonth))
		{
			bBad = true;
			nDay -= CDate::MonthSize(nYear,nMonth);
			nMonth++;
			if (nMonth > 12)
			{
				nMonth -= 12;
				nYear++;
			}
		}

		nYear -= m_nYear2;
		d1.Set(nYear,nMonth,nDay);
		if (d1.GetJD()<0 || bBad)
			dvBirth.Set(CDateValue::PHRASE,CDate(),CDate(),_T("not valid"));
		else
			dvBirth.Set(CDateValue::DATE,d1,CDate(),_T(""));
	}
}

BEGIN_EVENT_TABLE(CBirthCalc, wxDialog)
	//{{AFX_MSG_MAP(CBirthCalc)
//	ON_BN_CLICKED(IDC_DATE, OnDate)
//	ON_EN_CHANGE(IDC_DAYS, OnChangeDays)
//	ON_EN_CHANGE(IDC_MONTHS, OnChangeMonths)
//	ON_EN_CHANGE(IDC_YEARS, OnChangeYears)
//	ON_EN_CHANGE(IDC_YEARS2, OnChangeYears2)
//	ON_EN_CHANGE(IDC_YEARS3, OnChangeYears3)
//	ON_BN_CLICKED(IDC_AGE1, OnAge1)
//	ON_BN_CLICKED(IDC_AGE2, OnAge2)
//	ON_BN_CLICKED(IDC_AGE3, OnAge3)
	//}}AFX_MSG_MAP
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CBirthCalc message handlers

void CBirthCalc::OnDate() 
{
	CEditDate d;
	d.m_dv = m_dvDate;
	if (d.ShowModal()==IDOK)
	{
		m_dvDate = d.m_dv;
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
	}
}

void CBirthCalc::OnChangeDays() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

void CBirthCalc::OnChangeMonths() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

void CBirthCalc::OnChangeYears() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

void CBirthCalc::OnChangeYears2() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

void CBirthCalc::OnChangeYears3() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

void CBirthCalc::OnAge1() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

void CBirthCalc::OnAge2() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

void CBirthCalc::OnAge3() 
{
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
}

BOOL CBirthCalc::OnInitDialog() 
{
//	wxDialog::OnInitDialog();
	
//		UpdateData(TRUE); TransferFromWindow
//		UpdateData(FALSE); TransferToWindow
	
	return TRUE;
}
