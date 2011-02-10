#include "stdafx.h"
#include "gedtree.h"
#include "cita.h"
#include "gedtreedoc.h"
#include "gedline.h"
#include "util.h"

CCitation::CCitation(CGedtreeDoc* pDoc, HTREEITEM hti):
	m_pDoc(pDoc),
	m_hTreeItem(hti),
	m_nQuality(-1),
	m_iSource(-1)
{
}

void CCitation::Set(CGedtreeDoc* pDoc, HTREEITEM hti)
{
	m_pDoc = pDoc;
	m_hTreeItem = hti;
}

CCitation::~CCitation()
{
}

void CCitation::GetFromTree(const wxString& strSourceID)
{
	m_iSource = m_pDoc->LookupSource(strSourceID);

	wxTreeCtrl* tree = m_pDoc->m_tree;

    wxTreeItemId hSub = tree->GetRootItem();
    wxTreeItemIdValue cookie;
    hSub = tree->GetFirstChild(hSub,cookie);
	while (hSub.IsOk())
	{
		CGedLine* pglSub = (CGedLine*)tree->GetItemData(hSub);
		if (pglSub->m_strTok==_T("PAGE"))
		{
			m_strPage = pglSub->GetCleanValue(CGedLine::COLLAPSE);
		}
		else if (pglSub->m_strTok==_T("DATA"))
		{
            wxTreeItemIdValue cookie2;
            wxTreeItemId hSubSub = tree->GetFirstChild(hSub,cookie2);
			while (hSubSub.IsOk())
			{
				CGedLine* pglSub = (CGedLine*)tree->GetItemData(hSubSub);
				if (pglSub->m_strTok==_T("TEXT"))
				{
					m_strText = pglSub->m_strVal;
					m_pDoc->GetContLines(hSubSub,m_strText);
				}
        		hSubSub = tree->GetNextChild(hSubSub,cookie2);
			}
		}
		else if (pglSub->m_strTok==_T("QUAY"))
		{
			m_nQuality = pglSub->m_nVal;
		}
		hSub = tree->GetNextChild(hSub,cookie);
	}
}

void CCitation::Clear()
{
	m_iSource = -1;
	m_strPage.Empty();
	m_strText.Empty();
}

wxString CCitation::Display()
{
	wxString s;

	if (m_iSource>=0)
	{
		CSource& sour = m_pDoc->m_rSource[m_iSource];
//		wxString& strAuthor = sour.m_strAuthor;
		wxString& strTitle = sour.m_strTitle;

//		if (!strAuthor.IsEmpty())
//			s = strAuthor+". ";

		s += strTitle;
		if (strTitle.Right(1) != _T("."))
			s += _T(".");
	}
	else if (!m_strPage.IsEmpty() || !m_strText.IsEmpty())
	{
		s += _T("[Unknown source.]");
	}

	if (!m_strPage.IsEmpty())
	{
		s += _T(" ")+m_strPage;
		if (m_strPage.Right(1) != _T("."))
			s += _T(".");
	}

	return s;
}

void CCitation::PutToTree(HTREEITEM htiParent)
{
	wxASSERT(m_pDoc);

	if (m_iSource>=0 || !m_strPage.IsEmpty() || !m_strText.IsEmpty())
	{
		if (m_iSource>=0)
		{
			CSource& sour = m_pDoc->m_rSource[m_iSource];
			m_hTreeItem = m_pDoc->ResetSubValue(htiParent,_T("SOUR"),sour.GetID());
		}
		else
		{
			m_hTreeItem = m_pDoc->ResetSubValue(htiParent,_T("SOUR"),_T(""),FALSE,TRUE);
		}
		m_pDoc->ResetSubValue(m_hTreeItem,_T("PAGE"),m_strPage);
		if (m_nQuality>=0)
		{
			m_pDoc->ResetSubValue(m_hTreeItem,_T("QUAY"),CUtil::str(m_nQuality));
		}
		else
		{
			m_pDoc->DeleteSubValue(m_hTreeItem,_T("QUAY"));
		}
		if (m_strText.IsEmpty())
		{
			m_pDoc->DeleteSubValue(m_hTreeItem,_T("DATA"));
		}
		else
		{
			HTREEITEM hData = m_pDoc->ResetSubValue(m_hTreeItem,_T("DATA"),_T(""),FALSE,TRUE);
			m_pDoc->ResetSubValue(hData,_T("TEXT"),m_strText,FALSE,FALSE,TRUE);
		}
	}
	else
	{
		if (m_hTreeItem)
			m_pDoc->DeleteItem(m_hTreeItem);
		m_hTreeItem = NULL;
	}
}
