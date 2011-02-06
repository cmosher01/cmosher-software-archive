#ifndef header_trlr
#define header_trlr

#include "gedrecord.h"

class CTrailer : public CGedRecord
{
public:

	CTrailer(CGedtreeDoc* pDoc = NULL, HTREEITEM hTreeItem = NULL);
	void GetFromTree() { }
	void PutToTree();
	void Calc() { }
};

#endif
