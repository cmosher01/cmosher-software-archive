#include "stdafx.h"
#include "gedtree.h"
#include "attr.h"
#include "gedtreedoc.h"
#include "gedline.h"

CAttr::CAttr(CGedtreeDoc* pDoc, HTREEITEM htiAttr):
	m_pDoc(pDoc),
	m_hTreeItem(htiAttr),
	m_evt(pDoc,NULL)
{
}

void CAttr::Set(CGedtreeDoc* pDoc, HTREEITEM hti)
{
	m_pDoc = pDoc;
	m_hTreeItem = hti;
}

void CAttr::Calc()
{
	m_evt.Calc();
}

CAttr::~CAttr()
{
}

void CAttr::GetFromTree(const wxString& strTypeTok, const wxString& strValue)
{
	m_strTypeTok = strTypeTok;
	m_strValue = strValue;

	m_evt.Set(m_pDoc,m_hTreeItem);
	m_evt.GetFromTree(_T("EVEN"));

	CalcType();
}

void CAttr::PutToTree()
{
	m_evt.Set(m_pDoc,m_hTreeItem);
	m_evt.PutToTree();
	m_pDoc->ResetToken(m_hTreeItem,m_strTypeTok,m_strValue);
	m_pDoc->DeleteSubValue(m_hTreeItem,_T("TYPE"));// for now
}

void CAttr::CalcType()
{
	if (m_strTypeTok==_T("CAST"))
		m_strType = _T("caste");
	else if (m_strTypeTok==_T("DSCR"))
		m_strType = _T("description");
	else if (m_strTypeTok==_T("EDUC"))
		m_strType = _T("education");
	else if (m_strTypeTok==_T("IDNO"))
		m_strType = _T("national ID");
	else if (m_strTypeTok==_T("NATI"))
		m_strType = _T("national origin");
	else if (m_strTypeTok==_T("NCHI"))
		m_strType = _T("count of children");
	else if (m_strTypeTok==_T("NMR"))
		m_strType = _T("count of marriages");
	else if (m_strTypeTok==_T("OCCU"))
		m_strType = _T("occupation");
	else if (m_strTypeTok==_T("PROP"))
		m_strType = _T("posession");
	else if (m_strTypeTok==_T("RELI"))
		m_strType = _T("religion");
	else if (m_strTypeTok==_T("SSN"))
		m_strType = _T("US Social Security number");
	else if (m_strTypeTok==_T("TITL"))
		m_strType = _T("title");
}

void CAttr::Delete()
{
	wxASSERT(m_pDoc);

	m_pDoc->DeleteItem(m_hTreeItem);
}
