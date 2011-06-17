// LeftView.cpp : implementation of the CLeftView class
//

#include "stdafx.h"
#include "pdewin.h"

#include "pdewinDoc.h"
#include "LeftView.h"
#include "pdewinView.h"
#include "ProdosFile.h"
#include "DosFile.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CLeftView

IMPLEMENT_DYNCREATE(CLeftView, CTreeView)

BEGIN_MESSAGE_MAP(CLeftView, CTreeView)
	//{{AFX_MSG_MAP(CLeftView)
	ON_NOTIFY_REFLECT(TVN_SELCHANGED, OnSelchanged)
	ON_NOTIFY_REFLECT(TVN_BEGINDRAG, OnBegindrag)
	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()
	ON_MESSAGE(UM_DRAGGED_TO, OnDraggedTo)
	ON_MESSAGE(UM_DRAGGED_OFF, OnDraggedOff)
	ON_MESSAGE(UM_DROPPED_FILE, OnDroppedFile)
	ON_MESSAGE(UM_DROPPED_DIR, OnDroppedDir)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CLeftView construction/destruction

CLeftView::CLeftView():
	m_pDragImage(NULL),
	m_bDragging(FALSE),
	m_hDropItem(NULL),
	m_hWndDraggedToPrev(NULL),
	m_hCur(0)
{
}

CLeftView::~CLeftView()
{
	delete m_pDragImage;
}

	/*
	{
		CFile pf("doc1.dmp",CFile::modeCreate|CFile::modeWrite);
		CDumpContext dc(&pf);
		GetDocument()->Dump(dc);
	}
	*/

BOOL CLeftView::PreCreateWindow(CREATESTRUCT& cs)
{
	cs.style |= TVS_HASLINES|TVS_LINESATROOT|TVS_HASBUTTONS;
	return CTreeView::PreCreateWindow(cs);
}

/////////////////////////////////////////////////////////////////////////////
// CLeftView drawing

void CLeftView::OnDraw(CDC* pDC)
{
	CPdewinDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
}


void CLeftView::OnInitialUpdate()
{
	CTreeView::OnInitialUpdate();

	m_imageList.Create(16,16,ILC_COLORDDB,2,8);
	m_imageList.Add((HICON)LoadImage(AfxGetInstanceHandle(),MAKEINTRESOURCE(IDI_FOLDER),IMAGE_ICON,16,16,LR_LOADTRANSPARENT));
	m_imageList.Add((HICON)LoadImage(AfxGetInstanceHandle(),MAKEINTRESOURCE(IDI_OPENFOLD),IMAGE_ICON,16,16,LR_LOADTRANSPARENT));

	GetTreeCtrl().SetImageList(&m_imageList,TVSIL_NORMAL);
}

/////////////////////////////////////////////////////////////////////////////
// CLeftView diagnostics

#ifdef _DEBUG
void CLeftView::AssertValid() const
{
	CTreeView::AssertValid();
}

void CLeftView::Dump(CDumpContext& dc) const
{
	CTreeView::Dump(dc);
}

CPdewinDoc* CLeftView::GetDocument() // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CPdewinDoc)));
	return (CPdewinDoc*)m_pDocument;
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CLeftView message handlers

void CLeftView::OnUpdate(CView* pSender, LPARAM lHint, CObject* pHint) 
{
	GetTreeCtrl().SetRedraw(FALSE);
	GetDocument()->GetRightView()->GetListCtrl().SetRedraw(FALSE);

	m_hCur = TVI_ROOT;
	GetTreeCtrl().DeleteAllItems();

	if (GetDocument()->m_osType == osProdos)
	{
		TV_INSERTSTRUCT is;
		is.hParent = TVI_ROOT;
		is.hInsertAfter = TVI_LAST;
		is.item.mask = TVIF_IMAGE | TVIF_SELECTEDIMAGE | TVIF_TEXT | TVIF_PARAM;

		is.item.lParam = 0;//???
		is.item.iImage = 0;//???
		is.item.iSelectedImage = 1;//???
		is.item.pszText = "ProDOS boot loader";
		GetTreeCtrl().InsertItem(&is);

		is.item.lParam = 0;//???
		is.item.iImage = 0;//???
		is.item.iSelectedImage = 1;//???
		is.item.pszText = "SOS boot loader";
		GetTreeCtrl().InsertItem(&is);

		CProdosDir* pDir = &GetDocument()->m_dir;
		is.item.lParam = (DWORD)pDir;
		is.item.iImage = 0;
		is.item.iSelectedImage = 1;
		CString str = pDir->m_strName;
		FixCase(str);
		str = "Volume: "+str;
		is.item.pszText = (LPSTR)(LPCTSTR)str;
		HTREEITEM hDir = GetTreeCtrl().InsertItem(&is);
		if (pDir==m_pDirCurrent)
			m_hCur = hDir;

		AddSubDirs(pDir,hDir);

		GetTreeCtrl().Select(m_hCur,TVGN_CARET);
		m_hCur = 0;
	}
	else
	{
		TV_INSERTSTRUCT is;
		is.hParent = TVI_ROOT;
		is.hInsertAfter = TVI_LAST;
		is.item.mask = TVIF_IMAGE | TVIF_SELECTEDIMAGE | TVIF_TEXT | TVIF_PARAM;

		is.item.lParam = 0;//???
		is.item.iImage = 0;//???
		is.item.iSelectedImage = 1;//???
		is.item.pszText = "DOS 3.3";
		GetTreeCtrl().InsertItem(&is);

		CDosDir* pDir = &GetDocument()->m_dir2;
		is.item.lParam = (DWORD)pDir;
		is.item.iImage = 0;
		is.item.iSelectedImage = 1;
		CString str = pDir->m_strName;
//		FixCase(str);
//		str = "Volume: "+str;
		is.item.pszText = (LPSTR)(LPCTSTR)str;
		HTREEITEM hDir = GetTreeCtrl().InsertItem(&is);
		if (pDir==(CDosDir*)m_pDirCurrent)
			m_hCur = hDir;

		AddSubDirs(NULL,hDir);

		GetTreeCtrl().Select(m_hCur,TVGN_CARET);
		m_hCur = 0;
	}
	SetFocus();

	GetTreeCtrl().SetRedraw(TRUE);
	GetDocument()->GetRightView()->GetListCtrl().SetRedraw(TRUE);
}

void CLeftView::AddSubDirs(CProdosDir* pDir, HTREEITEM hParent)
{
	if (GetDocument()->m_osType == osProdos)
	{
		for (int i(0); i<pDir->m_rpFile.GetSize(); i++)
		{
			CProdosFile* pFile = pDir->m_rpFile[i];
			CProdosDir* pDirIfIsOne = pFile->m_pPddir;
			if (pDirIfIsOne)
			{
				TV_INSERTSTRUCT is;
				is.hParent = hParent;
				is.hInsertAfter = TVI_LAST;
				is.item.mask = TVIF_IMAGE | TVIF_SELECTEDIMAGE | TVIF_TEXT | TVIF_PARAM;
				is.item.lParam = (DWORD)pDirIfIsOne;
				is.item.iImage = 0;
				is.item.iSelectedImage = 1;
				CString str = pFile->m_strName;
				FixCase(str);
				is.item.pszText = (LPSTR)(LPCTSTR)str;
				HTREEITEM hDir = GetTreeCtrl().InsertItem(&is);
				if (pDirIfIsOne==m_pDirCurrent)
					m_hCur = hDir;
				AddSubDirs(pDirIfIsOne,hDir);
			}
		}
	}
	else
	{
		for (int i(0); i<GetDocument()->m_dir2.m_rpFile.GetSize(); i++)
		{
			CDosFile* pFile = GetDocument()->m_dir2.m_rpFile[i];
		}
	}
}

CProdosDir* CLeftView::GetCurrentDir()
{
	HTREEITEM hCur = GetTreeCtrl().GetSelectedItem();
	CProdosDir* pDir = NULL;
	if (hCur)
		pDir = GetItemFile(hCur);
	return pDir;
}

CDosDir* CLeftView::GetCurrentDir2()
{
	HTREEITEM hCur = GetTreeCtrl().GetSelectedItem();
	CDosDir* pDir = NULL;
	if (hCur)
		pDir = (CDosDir*)GetItemFile(hCur);
	return pDir;
}

void CLeftView::SetCurrentDir(CProdosDir* pDir)
{
	if (!m_hCur)
		m_pDirCurrent = pDir;
}

void CLeftView::OnSelchanged(NMHDR* pNMHDR, LRESULT* pResult) 
{
	NM_TREEVIEW* pNMTreeView = (NM_TREEVIEW*)pNMHDR;
	*pResult = 0;

	SetCurrentDir(GetCurrentDir());
	GetDocument()->UpdateAllViews(this);
}

void CLeftView::FixCase(CString& str)
{
	CString f = str.Left(1);
	f.MakeUpper();
	str.MakeLower();
	str = f+str.Mid(1);
}

void CLeftView::OnBegindrag(NMHDR* pNMHDR, LRESULT* pResult) 
{
	NM_TREEVIEW* pNMTreeView = (NM_TREEVIEW*)pNMHDR;
	*pResult = 0;

	// this is the list item we're dragging
	m_hDragItem = pNMTreeView->itemNew.hItem;
	//???GetTreeCtrl().SelectItem(m_hDragItem);

	// create a drag image
	delete m_pDragImage;
	CPoint pt(8,8);
	m_pDragImage = GetTreeCtrl().CreateDragImage(m_hDragItem);
	ASSERT (m_pDragImage);
	// changes the cursor to the drag image (DragMove() is still required in 
	// OnMouseMove())
	VERIFY(m_pDragImage->BeginDrag(0,CPoint(8, 8)));
	VERIFY(m_pDragImage->DragEnter(GetDesktopWindow(),pNMTreeView->ptDrag));

	// set dragging flag
	m_bDragging = TRUE;
	m_hDropItem = NULL;

	// capture all mouse messages
	SetCapture();
}

void CLeftView::OnLButtonUp(UINT nFlags, CPoint point) 
{
	if (m_bDragging)
	{
		CPoint pt(point);
		ClientToScreen(&pt);

		VERIFY(::ReleaseCapture());
		m_bDragging = FALSE;
		VERIFY(m_pDragImage->DragLeave(GetDesktopWindow()));
		m_pDragImage->EndDrag();

		if (m_hWndDraggedToPrev)
		{
			::SendMessage(m_hWndDraggedToPrev,UM_DRAGGED_OFF,0,0);
		}
		m_hWndDraggedToPrev = NULL;
		HWND hWndDraggedTo = ::WindowFromPoint(pt);
		if (hWndDraggedTo)
		{
			::SendMessage(hWndDraggedTo,UM_DROPPED_DIR,GetDocument()->m_osType,(LPARAM)GetItemFile(m_hDragItem));
		}
	}
	CTreeView::OnLButtonUp(nFlags, point);
}

void CLeftView::OnMouseMove(UINT nFlags, CPoint point) 
{
	if (m_bDragging)
	{
		CPoint pt (point);
		ClientToScreen (&pt);
		// move the drag image
		VERIFY (m_pDragImage->DragMove (pt));
		// unlock window updates
		VERIFY (m_pDragImage->DragShowNolock (FALSE));

		HWND hWndDraggedTo = ::WindowFromPoint(pt);
		if (m_hWndDraggedToPrev && hWndDraggedTo!=m_hWndDraggedToPrev)
		{
			::SendMessage(m_hWndDraggedToPrev,UM_DRAGGED_OFF,0,0);
		}
		m_hWndDraggedToPrev = hWndDraggedTo;
		if (hWndDraggedTo)
		{
			::SendMessage(hWndDraggedTo,UM_DRAGGED_TO,0,MAKELPARAM(pt.x,pt.y));
		}

		// lock window updates
		VERIFY (m_pDragImage->DragShowNolock (TRUE));
	}
	CTreeView::OnMouseMove(nFlags, point);
}

CProdosDir* CLeftView::GetItemFile(HTREEITEM hItem)
{
	return (CProdosDir*)(GetTreeCtrl().GetItemData(hItem));
}

void CLeftView::OnDraggedTo(WPARAM wParam, LPARAM lParam)
{
	CPoint pt(LOWORD(lParam),HIWORD(lParam));
	GetTreeCtrl().ScreenToClient(&pt);

	UINT uFlags;
	HTREEITEM hCurDropItem = GetTreeCtrl().HitTest(pt,&uFlags);

	if (hCurDropItem!=m_hDropItem)
	{
		HighlightDropItem(FALSE);
		m_hDropItem = hCurDropItem;
		HighlightDropItem(TRUE);
	}
}

void CLeftView::OnDraggedOff(WPARAM wParam, LPARAM lParam)
{
	HighlightDropItem(FALSE);
}

void CLeftView::HighlightDropItem(BOOL bHighlight)
{
	if (m_hDropItem==NULL)
		return;

	GetTreeCtrl().SelectDropTarget(
		bHighlight ? m_hDropItem : NULL);

	//GetTreeCtrl().UpdateWindow();
}

void CLeftView::OnDroppedFile(WPARAM wParam, LPARAM lParam)
{
	if (m_hDropItem==NULL)
		return;

	// user dropped a CProdosFile (which may
	// represent an actual file, or a subdirectory) from
	// a right-hand view onto one of our CProdosDirs
	CProdosFile* pSourceFile = (CProdosFile*)lParam;
	CProdosDir* pDestDir = GetItemFile(m_hDropItem);

	if (pDestDir==NULL)
	{
		AfxMessageBox("You cannot drop a file here. That feature has not been implemented.");
		return;
	}

	BOOL bS2 = (osType)wParam!=osProdos;
	CProdosDir* pSourceDirIfAny = NULL;

	if (!bS2)
		pSourceDirIfAny = pSourceFile->m_pPddir;

	if (pSourceDirIfAny==pDestDir)
		return;

	CDosFile* pSourceFile2 = (CDosFile*)pSourceFile;
	if (bS2)
	{
		if (pSourceFile2->m_pParent==(void*)pDestDir)
			return;
	}
	else
	{
		if (pSourceFile->m_pParent==pDestDir)
			return;
	}

	BOOL bD2 = GetDocument()->m_osType!=osProdos;
	CDosDir* pDestDir2 = (CDosDir*)pDestDir;

	CString strSourceName = (bS2?pSourceFile2->m_strName:pSourceFile->m_strName);
	CLeftView::FixCase(strSourceName);
	CString strSource = (bS2?pSourceFile2->GetFullName():pSourceFile->GetFullName());
	CString strDest = (bD2?pDestDir2->GetFullName():pDestDir->GetFullName())+"/"+strSourceName;
	CString str;
	str = "Do you want to copy the "+(bS2?pSourceFile2->GetTypeString():pSourceFile->GetTypeString())+" file:\n\n    "+strSource+"\n\nto:\n\n    "+strDest;
	if (AfxMessageBox(str,MB_YESNO)==IDNO)
		return;
	if (bD2)
	{
		CDosFile* pNewCopy;
		if (bS2)
			pNewCopy = new CDosFile(*pSourceFile2);
		else
			pNewCopy = new CDosFile(*pSourceFile);
		pNewCopy->m_pParent = pDestDir2;
		pDestDir2->m_rpFile.Add(pNewCopy);
	}
	else
	{
		CProdosFile* pNewCopy;
		if (bS2)
			pNewCopy = new CProdosFile(*pSourceFile2);
		else
			pNewCopy = new CProdosFile(*pSourceFile);
		pNewCopy->m_pParent = pDestDir;
		pDestDir->m_rpFile.Add(pNewCopy);
	}
	GetDocument()->SetModifiedFlag(TRUE);
	SetCurrentDir(pDestDir);
	GetDocument()->UpdateWindow();
}

void CLeftView::OnDroppedDir(WPARAM wParam, LPARAM lParam)
{
	if (m_hDropItem==NULL)
		return;

	// user dropped a CProdosDir (from a left view) onto
	// one of our CProdosDirs

	CProdosDir* pSourceDir = (CProdosDir*)lParam;
	CProdosDir* pDestDir = GetItemFile(m_hDropItem);

	if (pDestDir==NULL)
	{
		AfxMessageBox("You cannot drop a file here. That feature has not been implemented.");
		return;
	}

	if (pSourceDir==NULL)
	{
		AfxMessageBox("You cannot drag that file. That feature has not been implemented.");
		return;
	}

	if (pSourceDir==pDestDir)
		return;

	if (pSourceDir->m_pParent)
		if (pSourceDir->m_pParent->m_pParent==pDestDir)
			return;

	CString strSourceName = pSourceDir->m_strName;
	CLeftView::FixCase(strSourceName);
	CString strSource = pSourceDir->GetFullName();
	CString strDest = pDestDir->GetFullName()+"/"+strSourceName;
	CString str;
	str = "Do you want to copy:\n\n    "+strSource+"\n\nto:\n\n    "+strDest;
	if (AfxMessageBox(str,MB_YESNO)==IDNO)
		return;

	CProdosFile* pNewCopy;
	if (!pSourceDir->m_pParent)
	{
		// the source is probably the root dir,
		// so we need to construct a new CProdosFile and
		// use that one
		pNewCopy = new CProdosFile(NULL);
		CProdosDir* pNewDir = new CProdosDir(*pSourceDir);
		pNewDir->m_pParent = pNewCopy;
		pNewDir->m_bIsVolDir = FALSE;
		// fill in other fields here (pNewDir into pNewCopy)
		pNewCopy->m_bDeleted = FALSE;
		pNewCopy->m_nLen = 0;//???
		pNewCopy->m_strName = pNewDir->m_strName;
		pNewCopy->m_nFileType = 0x0F;
		pNewCopy->m_pddateCreation = pNewDir->m_pddateCreation;
		pNewCopy->m_nVersion = pNewDir->m_nVersion;
		pNewCopy->m_nMinVersion = pNewDir->m_nMinVersion;
		pNewCopy->m_nCanDestroy = pNewDir->m_nCanDestroy;
		pNewCopy->m_nCanRename  = pNewDir->m_nCanRename;
		pNewCopy->m_nCanBackup  = pNewDir->m_nCanBackup;
		pNewCopy->m_nCanWrite   = pNewDir->m_nCanWrite;
		pNewCopy->m_nCanRead    = pNewDir->m_nCanRead;
		pNewCopy->m_nAuxType = 0;
		pNewCopy->m_pddateModification = pNewDir->m_pddateCreation;
		pNewCopy->m_pPddir = pNewDir;
	}
	else
	{
		pNewCopy = new CProdosFile(*(pSourceDir->m_pParent));
	}
	pNewCopy->m_pParent = pDestDir;
	pDestDir->m_rpFile.Add(pNewCopy);
	SetCurrentDir(pDestDir);
	GetDocument()->SetModifiedFlag(TRUE);
	GetDocument()->UpdateWindow();
}
