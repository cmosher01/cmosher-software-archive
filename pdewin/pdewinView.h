// pdewinView.h : interface of the CPdewinView class
//
/////////////////////////////////////////////////////////////////////////////

#if !defined(AFX_PDEWINVIEW_H__56A05090_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
#define AFX_PDEWINVIEW_H__56A05090_25F6_11D3_9A02_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class CDosFile;
class CProdosFile;

class CPdewinView : public CListView
{
protected: // create from serialization only
	CPdewinView();
	DECLARE_DYNCREATE(CPdewinView)

// Attributes
public:
	CPdewinDoc* GetDocument();

	CImageList* m_pDragImage;
	BOOL		m_bDragging;
	int			m_nDragItem;
	int			m_nDropIndex;
	HWND m_hWndDraggedToPrev;

// Operations
public:
protected:
	void CheckColumnWidth(int nCol, const CString& str, BOOL bForce = FALSE);
	CProdosFile* GetItemFile(int nItem);
	void DeleteCurrentItem();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CPdewinView)
	public:
	virtual void OnDraw(CDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	protected:
	virtual void OnInitialUpdate(); // called first time after construct
	virtual void OnUpdate(CView* pSender, LPARAM lHint, CObject* pHint);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CPdewinView();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:
	CString GetSubItemString(CProdosFile* pFile, int iSubItem);
	CString GetSubItemString2(CDosFile* pFile, int iSubItem);
	void HighlightDropItem(BOOL bHighlight);

	CImageList m_imageList;

// Generated message map functions
protected:
	//{{AFX_MSG(CPdewinView)
	afx_msg void OnGetdispinfo(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnDblclk(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnBegindrag(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg void OnDraggedTo(WPARAM wParam, LPARAM lParam);
	afx_msg void OnDraggedOff(WPARAM wParam, LPARAM lParam);
	afx_msg void OnDroppedFile(WPARAM wParam, LPARAM lParam);
	afx_msg void OnDroppedDir(WPARAM wParam, LPARAM lParam);
	afx_msg void OnKeydown(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnEndlabeledit(NMHDR* pNMHDR, LRESULT* pResult);
	//}}AFX_MSG
	afx_msg void OnStyleChanged(int nStyleType, LPSTYLESTRUCT lpStyleStruct);
	DECLARE_MESSAGE_MAP()
};

#ifndef _DEBUG  // debug version in pdewinView.cpp
inline CPdewinDoc* CPdewinView::GetDocument()
   { return (CPdewinDoc*)m_pDocument; }
#endif

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_PDEWINVIEW_H__56A05090_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
