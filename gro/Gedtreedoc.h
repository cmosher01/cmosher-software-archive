// gedtreeDoc.h : interface of the CGedtreeDoc class
//
/////////////////////////////////////////////////////////////////////////////

#include "indi.h"
#include "fami.h"
#include "sour.h"
#include "repo.h"
#include "head.h"
#include "note.h"

#include <iostream>

class CGedtreeViewDL;
class CProgress;

const wxSize MAX_BOUNDS(0x7fffffff,0x7fffffff);
const int VIEW_BORDER(30);

class CArchive;

class CGedtreeDoc : public wxDocument
{
protected: // create from serialization only
	CGedtreeDoc();
	DECLARE_DYNCREATE(CGedtreeDoc)

// Attributes
public:
	wxString m_strUuid; // uuid created at doc open time
	UUID m_uuid; // uuid created at doc open time
	wxWindow* m_wndTree;
	wxTreeCtrl* m_tree;
	int m_iSourceLast;
	bool m_bHadXY;

	CHeader m_head;
	CMapSS m_mapschemaIndiTagToLabel;
	CMapSS m_mapschemaFamiTagToLabel;

	wxArrayCIndividual m_rIndividual;
	R_int m_rsortIndi;
	bool m_bsortIndiNeedsUpdate;
	CMapSI m_mapIDToIndividual;
	int LookupIndividual(const wxString& strID);

	wxArrayCFamily m_rFamily;
	CMapSI m_mapIDToFamily;
	int LookupFamily(const wxString& strID);

	wxArrayCSource m_rSource;
	CMapSI m_mapIDToSource;
	int LookupSource(const wxString& strID);

	wxArrayCRepository m_rRepository;
	CMapSI m_mapIDToRepository;
	int LookupRepository(const wxString& strID);

	wxArrayCNote m_rNote;
	CMapSI m_mapIDToNote;
	int LookupNote(const wxString& strID);

	int mcLevel; // and it's count of levels

	CIndividual* Individual(int i);
	CFamily* Family(int i);
	wxRect GetBounds();
	wxRect GetUnscaledBounds();
	wxRect GetUnscaledBoundsNoBorder();
	void CalcScale();
	void CalcNormalBounds();
	void CalcBounds();
	wxSize Normalize();
	HTREEITEM ResetSubValue(HTREEITEM hTreeItem, const wxString& strTok, const wxString& strVal,
		BOOL bAlwaysAdd = FALSE, BOOL bEvenIfEmpty = FALSE, BOOL bBreakIntoCont = FALSE);
	void DeleteSubValue(HTREEITEM hTreeItem, const wxString& strTok);
	void DeleteItem(HTREEITEM hTreeItem);
	void ResetToken(HTREEITEM hTreeItem, const wxString& strTok, const wxString& strVal = "");
	void GetContLines(HTREEITEM htiParent, wxString& strValue);
	void BreakContLines(const wxString& strValue, wxArrayString& rstrTok, wxArrayString& rstrVal);
	HTREEITEM InsertChild(const wxString& strTok, HTREEITEM hTreeItem, int cLev = 0, const wxString& strVal = "");
	BOOL HasChildren(const CGedRecord& recParent);
	BOOL HitsIndi(CIndividual* pIndiTest);

// Operations
public:
	void ShiftAllIndividuals(const wxSize& sizShift);
	void ClearAllIndividuals();
	void AutoClean();
	void Init();
	void OpenIndi(int iIndi);
	wxPoint GetNextPos();
	void ResetAllViews(UINT flagsChanged = -1);
	void UpdateTitle();
	void SetInputCharSet(const wxString& str);
	R_int* GetSortedIndis();
	void GetSortedSours(R_int& rSour);
	void SavePathToReg(int i);
	int PasteIndi(CIndividual& indiFrom);
	void FixUpPastedIndi(int i);
	int PasteFami(CFamily& famiFrom);
	int PasteSour(CSource& sourFrom);
	int PasteRepo(CRepository& repoFrom);
	int PasteNote(CNote& noteFrom);
	void ClearContents();
	void htmlIndex(int iLevel, int iBase, const std::wstring& docid, std::ostream& os);
//	void svgChart(const wstring& docid, thinsock& sock);
//	void appletChart(const std::wstring& docid, thinsock& sock);
	void clearAppletChart();
	void htmlIndi(int iIndi, const std::wstring& docid, std::ostream& os);
	void htmlSour(int iSour, const std::wstring& docid, std::ostream& os);
//	void webFile(const std::wstring& path, thinsock& os, std::wstring& mime);

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CGedtreeDoc)
	public:
	virtual void Serialize(CArchive& ar);
	virtual bool OnCloseDocument();
	virtual BOOL OnOpenDocument(LPCTSTR lpszPathName);
	virtual bool OnNewDocument();
	virtual BOOL OnSaveDocument(LPCTSTR lpszPathName);
	protected:
	virtual BOOL SaveModified();
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CGedtreeDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

	CChildFrame* m_pIndiList;
	CGedtreeViewDL* m_pDL;

protected:
	wxPoint m_pointNext;
	MyRect m_rectUnscaledBounds;
	MyRect m_rectBounds;
	MyRect m_rectUnscaledBoundsNoBorder;
	MyRect m_rectBoundsNoBorder;
	int m_cRootRecord;
	CProgress* m_pPrg;
	int m_nPrg;
	BOOL m_bAnsel;
	BOOL m_bUnicode;
	BOOL m_bUnicodeFlagInFile;
	BOOL m_bReverse;
	BOOL m_bUseLast;
	BYTE m_byteLast;
	WORD m_tucharLast;
	BOOL m_bBad;
	int m_cIndi;
	int m_cFami;
	int m_cSour;
	int m_cRepo;
	int m_cNote;
	char* m_pAppletChart;
	int m_cAppletChart;

// Generated message map functions
protected:
	//{{AFX_MSG(CGedtreeDoc)
	afx_msg void OnFileSaveAs();
	afx_msg void OnFileSave();
	afx_msg void OnEditResizedocument();
	afx_msg void OnViewSources();
	afx_msg void OnViewRepositories();
	afx_msg void OnViewIndexofindividuals();
	afx_msg void OnViewNotes();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()

	void ReadFromArchive(CArchive& ar);
	void WriteToArchive(CArchive& ar);
	void GetFromTree();
	void WriteItem(CArchive& ar, HTREEITEM hTreeItem);
	void WriteRecords(CArchive& ar, const wxString& strTok = "");
	BOOL GetNextTraverse(HTREEITEM& htvi, int* cLev = NULL);
	void DeleteGedLine(HTREEITEM hTreeItem);
	void BreakConcLines(const wxString& strValue, wxArrayString& rstrTok, wxArrayString& rstrVal);
	void ResetIDs();
	void TestUnicode(wxFile* pFile);
	BOOL GetLine(CArchive& ar, wxString& str);
	void PutLine(CArchive& ar, const wxString& str);
	wchar_t FilterReadChar(BYTE c);
	void DisconnectFile();
	void writeApplet(std::ostream& os);
};

/////////////////////////////////////////////////////////////////////////////
