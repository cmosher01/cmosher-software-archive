#ifndef header_note
#define header_note

#include "gedrecord.h"

class CNote : public CGedRecord
{
public:
	wxString m_strNote;

	CNote(CGedtreeDoc* pDoc = NULL, HTREEITEM hTreeItem = NULL);
	CNote& operator=(const CNote& o);
	CNote(const CNote& o);

	void Calc();
	void GetFromTree();
	void PutToTree();
};
WX_DECLARE_OBJARRAY(CNote,wxArrayCNote);

#endif
