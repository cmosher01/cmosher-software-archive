// LeftView.h : interface of the CLeftView class
//
/////////////////////////////////////////////////////////////////////////////

#if !defined(AFX_LEFTVIEW_H__56A05092_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
#define AFX_LEFTVIEW_H__56A05092_25F6_11D3_9A02_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class CPdewinDoc;
#include "ProdosDir.h"
class CDosDir;

class CLeftView : public CTreeView
{
protected: // create from serialization only
	CLeftView();
	DECLARE_DYNCREATE(CLeftView)

// Attributes
public:
	CPdewinDoc* GetDocument();
	CProdosDir* GetCurrentDir();
	CDosDir* GetCurrentDir2();
	void SetCurrentDir(CProdosDir* pDir);

	CImageList* m_pDragImage;
	BOOL		m_bDragging;
	HTREEITEM m_hDragItem;
	HTREEITEM m_hDropItem;
	HWND m_hWndDraggedToPrev;

	CProdosDir* m_pDirCurrent;
	HTREEITEM m_hCur;

// Operations
public:
	static void FixCase(CString& str);

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CLeftView)
	public:
	virtual void OnDraw(CDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	protected:
	virtual void OnInitialUpdate(); // called first time after construct
	virtual void OnUpdate(CView* pSender, LPARAM lHint, CObject* pHint);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CLeftView();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:
	void AddSubDirs(CProdosDir* pDir, HTREEITEM hParent = TVI_ROOT);
	void HighlightDropItem(BOOL bHighlight);
	CProdosDir* GetItemFile(HTREEITEM hItem);

	CImageList m_imageList;

// Generated message map functions
protected:
	//{{AFX_MSG(CLeftView)
	afx_msg void OnSelchanged(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnBegindrag(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg void OnDraggedTo(WPARAM wParam, LPARAM lParam);
	afx_msg void OnDraggedOff(WPARAM wParam, LPARAM lParam);
	afx_msg void OnDroppedFile(WPARAM wParam, LPARAM lParam);
	afx_msg void OnDroppedDir(WPARAM wParam, LPARAM lParam);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

#ifndef _DEBUG  // debug version in LeftView.cpp
inline CPdewinDoc* CLeftView::GetDocument()
   { return (CPdewinDoc*)m_pDocument; }
#endif

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_LEFTVIEW_H__56A05092_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
