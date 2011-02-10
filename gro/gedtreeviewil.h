// gedtreeViewIL.h : interface of the CGedtreeViewIL class
//
/////////////////////////////////////////////////////////////////////////////

#include "gedtreeview.h"

class CGedtreeDoc;
class CIndividual;

class CGedtreeViewIL : public CGedtreeView
{
protected: // create from serialization only
	CGedtreeViewIL();
	DECLARE_DYNCREATE(CGedtreeViewIL)

// Attributes
public:
	BOOL m_bInit;
	CListCtrl m_list;

// Operations
public:
	void Init();
	wxString GetWindowTitle();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CGedtreeViewIL)
	public:
	virtual void OnDraw(wxDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	virtual void OnInitialUpdate();
	protected:
	virtual BOOL OnPreparePrinting(CPrintInfo* pInfo);
	virtual void OnBeginPrinting(wxDC* pDC, CPrintInfo* pInfo);
	virtual void OnEndPrinting(wxDC* pDC, CPrintInfo* pInfo);
	//}}AFX_VIRTUAL
	afx_msg void OnGetdispinfoList(NMHDR* pNMHDR, LRESULT* pResult);

// Implementation

public:
	virtual ~CGedtreeViewIL();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:
	void Reset(UINT flagsChanged = -1);
	void InitCtrls();
	void PositionControls(int cx, int cy);
	void CheckColumnWidth(int nCol, const wxString& str, BOOL bForce = FALSE);
	void Fill();
	void DeleteData();

// Generated message map functions
protected:
	//{{AFX_MSG(CGedtreeViewIL)
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg void OnDestroy();
	afx_msg void OnSetFocus(wxWindow* pOldWnd);
	afx_msg void OnViewIndi();
	afx_msg void OnUpdateViewIndi(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnViewOpenpedigree();
	afx_msg void OnUpdateViewOpenpedigree(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnUpdateViewIndexofindividuals(wxUpdateUIEvent& pCmdUI);
	//}}AFX_MSG
	afx_msg void OnColumnclickList(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnDblclk(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnKeydownList(NMHDR* pNMHDR, LRESULT* pResult);

	DECLARE_EVENT_TABLE()
};
/////////////////////////////////////////////////////////////////////////////
