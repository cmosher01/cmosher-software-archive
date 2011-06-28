#ifndef header_sour
#define header_sour

#include "gedrecord.h"

class CSource : public CGedRecord
{
public:
	wxString m_strAuthor;
	wxString m_strTitle;
	wxString m_strPublish;
	wxString m_strText;
	int m_iRepository;

	CSource(CGedtreeDoc* pDoc = NULL, HTREEITEM hTreeItem = NULL);
	CSource(const CSource& o);
	CSource& operator=(const CSource& o);
	void GetFromTree();
	void Calc() { }
	void PutToTree();
	wxString GetDisplay();
	wxString GetWebPage(wxFile& tpl, const wxString& sDocID = "");
	wxString GetRTF(wxFile& tpl);
	wxString TextBlock(const wxString& strText);
	wxString WordBreak(const wxString& strText);
	wxString RTFTextBlock(const wxString& strText);
	void ConvertToHTML();
	wxString ConvertLine(const wxString& sline, const wxString& strRest, bool& in_table, int& ccol);
	wxString ConvertTD(const wxString& s, int ccol);
	int CountColumns(const wxString& sline, const wxString& srest);
	int CountColumnsRow(const wxString& s);
};
WX_DECLARE_OBJARRAY(CSource,wxArrayCSource);

#endif
