// gedtreeViewDL.h : interface of the CGedtreeViewDL class
//
/////////////////////////////////////////////////////////////////////////////

#include <afxcview.h>
#include "gedtreeview.h"
#include <vector>

class CGedtreeDoc;
class CIndividual;

typedef vector<CIndividual*> rpi_t;

class CGedtreeViewDL : public CGedtreeView
{
protected: // create from serialization only
	CGedtreeViewDL();
	DECLARE_DYNCREATE(CGedtreeViewDL)

// Attributes
public:
	wxRect m_rectPrev;
	wxPoint m_pointPrev;
	BOOL m_bSelecting;
	BOOL m_bMoving;
	int m_nScale;
	wxString m_strLastFind;
	bool m_bCtrl;
	bool m_bShift;
	int m_iLastSel;
	HGDIOBJ m_hdib;
	wxDC* m_pdcUse;
	COLORREF m_colorFamily;
	CPen* m_ppen;
	int m_dibWidth;
	int m_dibHeight;
	bool m_bSomeHidden;
	CDate* m_pAsOfDate;

	CIndividual* HitIndiTest(const wxPoint& point);
	void SelectHitIndis(const wxRect& rect);
	wxRect ShiftSelectedIndis(const wxSize& sizShift);
	BOOL IndiSelected();
	BOOL OneIndiSelected();

// Operations
public:
	wxString GetWindowTitle();
	void DeselectAll();
	void SetTotalSize();
	BOOL IsOut(const wxPoint& point);
	void ScrollToward(const wxPoint& point);
	void SetScale(int nScale);
	void ScrollToIndi(CIndividual* pIndi);
	BOOL DoFind(BOOL bFromSelection = FALSE);
	void Reset(UINT flagsChanged = -1);
	void ResetAllViews();
	void PostNextMove(UINT nFlags, wxPoint point);
	void OldGetUserTpl(wxFile& tpl, const wxString& strFilePath, const char* sTpl);
	void MoveSelectionUp();
	void MoveSelectionDown();
	void MoveSelectionLeft();
	void MoveSelectionRight();
	void MoveSelectionUpF();
	void MoveSelectionDownF();
	void MoveSelectionLeftF();
	void MoveSelectionRightF();
	void CleanAll();
	double GetScale();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CGedtreeViewDL)
	public:
	virtual void OnDraw(wxDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	virtual void OnInitialUpdate();
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CGedtreeViewDL();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

	void Scrolltoselection();
	wxPoint GetScrollPoint();

protected:
	void MyPrint(CMyDC& dc);
	void PrintPages(CMyDC& dc, const MyRect& draw);

// Generated message map functions
protected:
	//{{AFX_MSG(CGedtreeViewDL)
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg void OnLButtonDown(UINT nFlags, wxPoint point);
	afx_msg void OnMouseMove(UINT nFlags, wxPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, wxPoint point);
	afx_msg void OnEditAlignTop();
	afx_msg void OnUpdateEditAlignTop(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnEditClean();
	afx_msg void OnUpdateEditClean(wxUpdateUIEvent& pCmdUI);
	afx_msg BOOL OnEraseBkgnd(wxDC* pDC);
	afx_msg void OnEditSelectAncestors();
	afx_msg void OnEditSelectAll();
	afx_msg void OnUpdateEditSelectAncestors(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnViewZoomOut();
	afx_msg void OnViewZoomNormal();
	afx_msg void OnViewScrolltoselection();
	afx_msg void OnUpdateViewScrolltoselection(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnLButtonDblClk(UINT nFlags, wxPoint point);
	afx_msg void OnEditFind();
	afx_msg void OnEditFindnext();
	afx_msg void OnUpdateEditFindnext(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnEditDisconnect();
	afx_msg void OnUpdateEditDisconnect(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnEditNewindividual();
	afx_msg void OnEditConnect();
	afx_msg void OnUpdateEditConnect(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnViewOpenpedigree();
	afx_msg void OnUpdateViewOpenpedigree(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnViewDroplinechart();
	afx_msg void OnViewIndi();
	afx_msg void OnUpdateViewIndi(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnFileHTML();
	afx_msg void OnFileRTF();
	afx_msg void OnContextMenu(wxWindow* pWnd, wxPoint point);
	afx_msg void OnViewCensus();
	afx_msg void OnUpdateViewCensus(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnViewPlace();
	afx_msg void OnUpdateViewPlace(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnViewAnomalies();
	afx_msg void OnUpdateViewAnomalies(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnChar(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnKeyDown(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnKeyUp(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnFilePrint();
	afx_msg void OnEditHideunselected();
	afx_msg void OnUpdateEditHideunselected(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnEditShowall();
	afx_msg void OnUpdateEditShowall(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnEditShowAsOf();
	afx_msg void OnUpdateEditShowAsOf(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnEditCopy();
	afx_msg void OnUpdateEditCopy(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnEditPaste();
	afx_msg void OnUpdateEditPaste(wxUpdateUIEvent& pCmdUI);
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};
/////////////////////////////////////////////////////////////////////////////
