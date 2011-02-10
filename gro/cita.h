#ifndef _citation_
#define _citation_

#include "datevalue.h"

class CGedtreeDoc;

class CCitation
{
public:
	HTREEITEM m_hTreeItem;
	CGedtreeDoc* m_pDoc;

	int m_iSource;
	wxString m_strPage;
	wxString m_strText;
	int m_nQuality;

public:
	CCitation(CGedtreeDoc* pDoc = NULL, HTREEITEM htiEvent = NULL);
	void Set(CGedtreeDoc* pDoc, HTREEITEM htiEvent);
	~CCitation();

	void Clear();
	void Calc() { }
	wxString Display();

	void GetFromTree(const wxString& strTypeTok);
	void PutToTree(HTREEITEM htiParent);
};

#endif
