// Connect.cpp : implementation file
//

#include "stdafx.h"
#include "gedtree.h"
#include "Connect.h"
#include "gedtreedoc.h"
#include "indi.h"
#include "editsex.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CConnect dialog


CConnect::CConnect(CGedtreeDoc* pDoc, wxWindow* pParent /*=NULL*/)
	: m_pDoc(pDoc)
{
	//{{AFX_DATA_INIT(CConnect)
	m_nRelation = 0;
	m_nFamily = -1;
	m_strName0 = _T("");
	m_strName1 = _T("");
	//}}AFX_DATA_INIT
}


void CConnect::DoDataExchange(CDataExchange* pDX)
{
//	wxDialog::DoDataExchange(pDX);

	if (!pDX->m_bSaveAndValidate)
	{
		m_strName0 = m_pIndi0->Name();
		m_strName1 = m_pIndi1->Name();
		if (m_strName0==m_strName1)
		{
			if (m_pIndi0->m_revt.GetCount())
			{
				CEvt& evt = m_pIndi0->m_revt[0];
				m_strName0 +=
					_T(" (")+
					evt.m_strType+_T(": ")+
					evt.m_dvDate.Display(DATE_SHORTDATE)+_T(", ")+
					evt.m_strPlace+
					_T(")");
			}
			if (m_pIndi1->m_revt.GetCount())
			{
				CEvt& evt = m_pIndi1->m_revt[0];
				m_strName1 +=
					_T(" (")+
					evt.m_strType+_T(": ")+
					evt.m_dvDate.Display(DATE_SHORTDATE)+_T(", ")+
					evt.m_strPlace+
					_T(")");
			}
		}
	}

	//{{AFX_DATA_MAP(CConnect)
	DDX_Control(pDX, IDC_FAMILY, m_comboFamily);
	DDX_Radio(pDX, IDC_PARENT, m_nRelation);
	DDX_CBIndex(pDX, IDC_FAMILY, m_nFamily);
	DDX_Text(pDX, IDC_INDI0, m_strName0);
	DDX_Text(pDX, IDC_INDI1, m_strName1);
	//}}AFX_DATA_MAP

	FillFamilies();
}

void CConnect::AddFamily(int iFamily, int iShow)
// iShow: 0=husband, 1=wife, 2=child
{
	CFamily& fami = m_pDoc->m_rFamily[iFamily];

	wxString s;
	if (iShow==0)
	{
		if (fami.m_iHusband>=0)
			s = _T("by husband ")+m_pDoc->m_rIndividual[fami.m_iHusband].Name();
	}
	else if (iShow==1)
	{
		if (fami.m_iWife>=0)
			s = _T("by wife ")+m_pDoc->m_rIndividual[fami.m_iWife].Name();
	}

	if (s.IsEmpty())
	{
		if (fami.m_riChild.GetCount())
			s = _T("with child ")+m_pDoc->m_rIndividual[fami.m_riChild[0]].Name();
	}

	if (s.IsEmpty())
	{
		if (fami.m_revt.GetCount())
		{
			CEvt& evt = fami.m_revt[0];
			s =
				evt.m_strType+_T(": ")+
				evt.m_dvDate.Display(DATE_SHORTDATE)+_T(", ")+
				evt.m_strPlace;
		}
	}

	if (s.IsEmpty()) s = _T("[empty family]");

	AddFamilyItem(s,iFamily);
}

void CConnect::AddFamilyItem(const wxString& str, int iFamily)
{
	int i = m_comboFamily->Append(str);
	m_comboFamily->SetClientData(i,(void*)iFamily);
}

void CConnect::AddSpouseFamilies(CIndividual* pIndi)
{
	for (int i(0); i<pIndi->m_riSpouseToFamily.GetCount(); i++)
	{
		int iFami = pIndi->m_riSpouseToFamily[i];
		CFamily& fami = m_pDoc->m_rFamily[iFami];
		if (fami.m_iHusband<0||fami.m_iWife<0)
			AddFamily(iFami,2);
	}
}

void CConnect::FillParentChild(CIndividual* pIndiParent, CIndividual* pIndiChild)
{
	if (!pIndiParent->m_nSex)
	{
		AddFamilyItem(_T("Parent's gender unknown."),-2);
	}
	else
	{
		BOOL bMale = pIndiParent->m_nSex==1;

		int iParent = bMale ? pIndiChild->m_iFather : pIndiChild->m_iMother;
		if (iParent>=0)
		{
			wxString s;
			s += pIndiChild->Name();
			s += _T(" already has a ");
			s += bMale ? _T("father") : _T("mother");
			s += _T(": ");
			s += m_pDoc->m_rIndividual[iParent].Name();
			AddFamilyItem(s,-2);
		}
		else
		{
			// if child has a stub child-to-family
			if (pIndiChild->m_iChildToFamily>=0)
			{
				// we must use it
				AddFamilyItem(_T("(add parent to child's existing family)"),pIndiChild->m_iChildToFamily);
			}
			else
			{
				// add parent's spouse-to-families, showing other spouse
				for (int i(0); i<pIndiParent->m_riSpouseToFamily.GetCount(); i++)
					AddFamily(pIndiParent->m_riSpouseToFamily[i],bMale);
				wxString strNew = _T("by a new ");
				strNew += bMale ? _T("wife") : _T("husband");
				AddFamilyItem(strNew,-1);
			}
		}
	}
}

void CConnect::AddSiblingFamilies(CIndividual* pIndiSour, CIndividual* pIndiDest)
{
	wxString s;
	s += _T("(add ");
	s += pIndiSour->Name();
	s += _T(" to ");
	s += pIndiDest->Name();
	s += _T("'s family)");

	AddFamilyItem(s,pIndiDest->m_iChildToFamily);
}

void CConnect::CheckSex(CIndividual* pIndi)
{
	if (!pIndi->m_nSex)
	{
		CEditSex d;

		d.m_nSex = pIndi->m_nSex;
		int nButton = d.ShowModal();
		if (nButton==IDOK)
			pIndi->SetSex(d.m_nSex);
	}
}

void CConnect::FillFamilies()
{
	int curSel(0);
	m_comboFamily->Clear();
	if (m_nRelation==parent)
	{
		FillParentChild(m_pIndi0,m_pIndi1);
	}
	else if (m_nRelation==child)
	{
		FillParentChild(m_pIndi1,m_pIndi0);
	}
	else if (m_nRelation==spouse)
	{
		if (!m_pIndi0->m_nSex||!m_pIndi1->m_nSex)
		{
			AddFamilyItem(_T("Gender unknown."),-2);
		}
		else if (m_pIndi0->m_nSex==m_pIndi1->m_nSex)
		{
			AddFamilyItem(_T("Individuals of like gender cannot be partners."),-2);
		}
		else
		{
			AddFamilyItem(_T("(create a new family)"),-1);
			AddSpouseFamilies(m_pIndi0);
			AddSpouseFamilies(m_pIndi1);
			if (m_comboFamily->GetCount()>1)
				curSel = 1;
		}
	}
	else if (m_nRelation==sibling)
	{
		BOOL bHas0 = m_pIndi0->m_iChildToFamily>=0;
		BOOL bHas1 = m_pIndi1->m_iChildToFamily>=0;
		if (bHas0&&bHas1)
			AddFamilyItem(_T("Both individuals are already children"),-2);
		else if (bHas0)
			AddSiblingFamilies(m_pIndi1,m_pIndi0);
		else if (bHas1)
			AddSiblingFamilies(m_pIndi0,m_pIndi1);
		else
			AddFamilyItem(_T("(create a new family)"),-1);
	}
	m_comboFamily->SetSelection(curSel);
}

BEGIN_EVENT_TABLE(CConnect, wxDialog)
	//{{AFX_MSG_MAP(CConnect)
//	ON_BN_CLICKED(IDC_PARENT, OnRelation)
//	ON_BN_CLICKED(IDC_CHILD, OnRelation)
//	ON_BN_CLICKED(IDC_SPOUSE, OnRelation)
//	ON_BN_CLICKED(IDC_SIBLING, OnRelation)
	//}}AFX_MSG_MAP
END_EVENT_TABLE()

/////////////////////////////////////////////////////////////////////////////
// CConnect message handlers

void CConnect::OnRelation()
{
//	UpdateData(); TransferFromWindow
}

void CConnect::OnOK() 
{
//	UpdateData(); TransferFromWindow
	if ((int)m_comboFamily->GetClientData(m_nFamily)<-1)
	{
		AfxMessageBox(_T("You have chosen an invalid relation."));
	}
	else
	{
		m_nFamily = (int)m_comboFamily->GetClientData(m_nFamily);
		EndDialog(IDOK);
	}
}
