#ifndef _attr_
#define _attr_

#include "event.h"

class CGedtreeDoc;

class CAttr
{
public:
	HTREEITEM m_hTreeItem;
	CGedtreeDoc* m_pDoc;

	wxString m_strTypeTok;
	wxString m_strType;
	wxString m_strValue;
	CEvt m_evt;

	CAttr(CGedtreeDoc* pDoc = NULL, HTREEITEM htiAttr = NULL);
	~CAttr();
	void Set(CGedtreeDoc* pDoc, HTREEITEM hTreeItem);
	void CalcType();
	void Calc();

	void GetFromTree(const wxString& strTypeTok, const wxString& strValue);
	void PutToTree();
	void Delete();
};
WX_DECLARE_OBJARRAY(CAttr,wxArrayCAttr);

#endif
