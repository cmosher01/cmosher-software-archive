#ifndef _name_
#define _name_

#include "cita.h"

class CName
{
public:
	HTREEITEM m_hTreeItem;
	CGedtreeDoc* m_pDoc;

	wxString m_strDisplay;	// John Q. /Public/, II
	wxString m_strName;		// John Q. Public, II
	wxString m_strSurname;	// Public
	wxString m_strRest;		// John Q. ~, II
	wxString m_strGiven;		// John Q.
	wxString m_strSuffix;	// , II
	CCitation m_cita;

public:
	CName(CGedtreeDoc* pDoc = NULL, HTREEITEM htiEvent = NULL);
	void Set(CGedtreeDoc* pDoc, HTREEITEM htiEvent);
	~CName();
	void Calc();
	wxString Name();
	wxString NameSlashes();
	wxString Surname();
	wxString GivenName();
	wxString Filter(const wxString& strName);
	void GetFromTree(const wxString& strName);
	void PutToTree(HTREEITEM htiParent);
};

#endif
