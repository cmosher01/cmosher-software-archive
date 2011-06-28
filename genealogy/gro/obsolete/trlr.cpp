#include "stdafx.h"
#include "trlr.h"
#include "gedtree.h"
#include "gedtreedoc.h"
#include "gedline.h"

CTrailer::CTrailer(CGedtreeDoc* pDoc, HTREEITEM hTreeItem):
	CGedRecord(pDoc,hTreeItem)
{
}

void CTrailer::PutToTree()
{
	m_pDoc->ResetSubValue(TVI_ROOT,"TRLR","",FALSE,TRUE);
}
