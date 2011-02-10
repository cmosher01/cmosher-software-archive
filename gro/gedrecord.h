#ifndef gedrecord_fami
#define gedrecord_fami

class CGedtreeDoc;

class CGedRecord
{
public:
	wxString m_strID;
	int m_i;
	CGedtreeDoc* m_pDoc;
	HTREEITEM m_hTreeItem;

	CGedRecord(CGedtreeDoc* pDoc = NULL, HTREEITEM hTreeItem = NULL);
	CGedRecord(const CGedRecord& o);
	CGedRecord& operator=(const CGedRecord& o);
	~CGedRecord();
	static char GetPrefix(const wxString& strTok);
	wxString GetID();
	void SetIndex(int i);
	void CalcID();
	void Delete();
	BOOL Exists();
	void Set(CGedtreeDoc* pDoc, HTREEITEM hTreeItem);
	wxString GetWebFilePath(const wxString& sDocID = "");
	wxString GetLink(const wxString& strText = "", const wxString& sDocID = "");

	virtual void Calc() = 0;
	virtual wxString GetLinkText() { return m_strID; }
};

#endif
