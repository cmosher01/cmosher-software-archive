#ifndef BIGVIEW
#define BIGVIEW 1

// BigView.h : interface of the CBigScrollView class
//
/////////////////////////////////////////////////////////////////////////////

#define MM_NONE 0	// from MFC viewscrl.cpp

// [vlu] = virtual logical units = logical units + offset

#if 0
class CBigScrollView : public wxScrolledWindow
{
protected: // create from serialization only
	CBigScrollView();

// Attributes
public:
	COLORREF m_colorBackGnd;

protected:
	wxSize  m_TotalSize;		// logical BigScrollView size [vlu]
	BOOL   m_bExtendX;		// TRUE if BigScrollSize > ScrollSize
	BOOL   m_bExtendY;		// TRUE if BigScrollSize > ScrollSize
	long   m_Delta;			// to correct position between DP and LP
	wxPoint m_Offset;		// document offset [vlu]
	wxPoint m_Center;		// document center [vlu]
	wxSize  m_Ratio;			// scale factor to restore client center
							// simply using wxSize for convenience
// Operations
public:

protected:
// Overrides
	void   SetScrollSizes(int nMapMode, SIZE sizeTotal, const SIZE& sizePage = sizeDefault, const SIZE& sizeLine = sizeDefault);
	void   SetScaleToFitSize(SIZE sizeTotal);		// sizeTotal [vlu]
	void   FillOutsideRect(wxDC* pDC, wxBrush* pBrush);
	wxSize  GetTotalSize() const;					// [vlu]
	wxPoint GetScrollPosition() const;				// [vlu]
	void   ScrollToPosition(POINT Point);			// Point [vlu]
	void   CenterOnPoint(wxPoint Center);			// Center [vlu]

// Helpers
	wxPoint GetDeviceOrg() const;					// [du]
	void   GetLogClientRect(wxRect *pRect) const;	// [vlu]
	wxPoint GetLogPosition(wxPoint Point) const;		// [vlu]
	wxPoint GetLogClientCenter() const;				// [vlu]

	void SaveClientCenter();
	void RestoreClientCenter();
	
// Overrides
	virtual BOOL OnScroll(UINT nScrollCode, UINT nPos, BOOL bDoScroll = TRUE);
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CBigScrollView)
	public:
//	virtual void OnPrepareDC(wxDC* pDC, CPrintInfo* pInfo = NULL);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CBigScrollView();
//#ifdef _DEBUG
//	virtual void AssertValid() const;
//	virtual void Dump(CDumpContext& dc) const;
//#endif

protected:

// Generated message map functions
protected:
	//{{AFX_MSG(CBigScrollView)
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg BOOL OnEraseBkgnd(wxDC* pDC);
	afx_msg void OnHScroll(UINT nSBCode, UINT nPos, CScrollBar* pScrollBar);
	afx_msg void OnVScroll(UINT nSBCode, UINT nPos, CScrollBar* pScrollBar);
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};

/////////////////////////////////////////////////////////////////////////////
#endif
#define CBigScrollView wxScrolledWindow
#endif
