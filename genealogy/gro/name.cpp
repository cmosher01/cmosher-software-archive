#include "stdafx.h"
#include "gedtree.h"
#include "name.h"
#include "gedtreedoc.h"
#include "gedline.h"

static const char cSurnameDelim('/');

CName::CName(CGedtreeDoc* pDoc, HTREEITEM hti):
	m_pDoc(pDoc),
	m_hTreeItem(hti),
	m_cita(m_pDoc,NULL)
{
}

void CName::Set(CGedtreeDoc* pDoc, HTREEITEM hti)
{
	m_pDoc = pDoc;
	m_hTreeItem = hti;
}

CName::~CName()
{
}

void CName::GetFromTree(const wxString& strName)
{
	wxTreeCtrl& tree = m_pDoc->m_tree;

	m_strDisplay = strName;

	HTREEITEM hSub = tree.GetChildItem(m_hTreeItem);
	while (hSub)
	{
		HTREEITEM hNext = tree.GetNextSiblingItem(hSub);

		CGedLine* pglSub = (CGedLine*)tree.GetItemData(hSub);
		if (pglSub->m_strTok=="SOUR")
		{
			m_cita.Set(m_pDoc,hSub);
			m_cita.GetFromTree(pglSub->m_strValAsID);
		}
		else if (pglSub->m_strTok=="NPFX")
			m_pDoc->DeleteItem(hSub);
		else if (pglSub->m_strTok=="GIVN")
			m_pDoc->DeleteItem(hSub);
		else if (pglSub->m_strTok=="NICK")
			m_pDoc->DeleteItem(hSub);
		else if (pglSub->m_strTok=="SPFX")
			m_pDoc->DeleteItem(hSub);
		else if (pglSub->m_strTok=="SURN")
			m_pDoc->DeleteItem(hSub);
		else if (pglSub->m_strTok=="NSFX")
			m_pDoc->DeleteItem(hSub);

		hSub = hNext;
	}
	Calc();
}

void CName::Calc()
{
	// Parse the name.
	//
	// The name is in m_strDisplay, and we assume it has
	// slashes surrounding the surname.
	//
	// Pull out the surname into m_strSurname and put the
	// rest into m_strRest (if the surname was not at the end,
	// then put a ~ into m_strRest where the surname goes).
	//
	// Also, fill m_strName with the name without slashes.

	m_strSurname.Empty();
	m_strRest.Empty();
	m_strName.Empty();

	//  John Q. /Public/, II
	int i1 = m_strDisplay.Find(cSurnameDelim);
	if (i1==-1) 
	{
		// There are no slashes in the name.
		// We assume last word is surname and re-parse.
		// Note that this is the only case in which we
		// modify m_strDisplay.
		i1 = m_strDisplay.ReverseFind(' ');
		if (i1==-1)
			m_strDisplay += "//";
		else
			m_strDisplay =
				m_strDisplay.Left(i1+1)+"/"+
				m_strDisplay.Mid(i1+1)+"/";
		Calc();
		return;
	}

	m_strRest = m_strDisplay.Left(i1);	//  "John Q. "
	m_strName = m_strRest;
	m_strGiven = m_strName;

	wxString strRest = m_strDisplay.Mid(i1+1);//  "Public/, II"

	int i2 = strRest.Find(cSurnameDelim);
	if (i2==-1)
		m_strSurname = "";//strRest;
	else
		m_strSurname = strRest.Left(i2);

	m_strName.Trim(false);
	m_strName.Trim();
	m_strName += " ";
	m_strSurname.Trim(false);
	m_strSurname.Trim();
	m_strName += m_strSurname;

	wxString strPart;
	if (i2>=strRest.GetLength())
		strPart = "";
	else
		strPart = strRest.Mid(i2+1);

	if (!strPart.IsEmpty())
	{
		m_strName += strPart;
		m_strSuffix = strPart;
		m_strRest += "~"+strPart;
	}

	m_strSurname.Trim(false);
	m_strRest.Trim(false);
	m_strName.Trim(false);
	m_strGiven.Trim(false);
	m_strSuffix.Trim(false);
	m_strSurname.Trim();
	m_strRest.Trim();
	m_strName.Trim();
	m_strGiven.Trim();
	m_strSuffix.Trim();
}

wxString CName::Name()
{
	return Filter(m_strName);
//	return wxString("<")+m_strGiven+"> /"+m_strSurname+"/"+m_strSuffix;
}

wxString CName::NameSlashes()
{
	return Filter(m_strDisplay);
}

wxString CName::Surname()
{
	return Filter(m_strSurname);
}

wxString CName::GivenName()
{
	return Filter(m_strRest);
}

wxString CName::Filter(const wxString& strName)
{
	if (strName.IsEmpty())
		return "[unknown]";
	else
		return strName;
}

void CName::PutToTree(HTREEITEM htiParent)
{
	wxASSERT(m_pDoc);

	m_hTreeItem = m_pDoc->ResetSubValue(htiParent,"NAME",m_strDisplay);
	m_cita.PutToTree(m_hTreeItem);
}
