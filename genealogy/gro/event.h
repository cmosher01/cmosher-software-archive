#ifndef _event_
#define _event_

#include "datevalue.h"
#include "cita.h"

class CGedtreeDoc;

class CEvt
{
public:
	HTREEITEM m_hTreeItem;
	CGedtreeDoc* m_pDoc;

	wxString m_strTypeTok;
	wxString m_strType;
	CDateValue m_dvDate;
	wxString m_strPlace;
	CCitation m_cita;
	wxString m_strNote;
	int m_iNote;

	CEvt(CGedtreeDoc* pDoc = NULL, HTREEITEM htiEvent = NULL);
	~CEvt();
	void Set(CGedtreeDoc* pDoc, HTREEITEM hTreeItem);
	void CalcType();
	void Calc();

	void GetFromTree(const wxString& strTypeTok);
	void PutToTree();
	void Delete();
};
WX_DECLARE_OBJARRAY(CEvt,wxArrayCEvt);

#endif
