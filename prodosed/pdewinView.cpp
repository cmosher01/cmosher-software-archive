// pdewinView.cpp : implementation of the CPdewinView class
//

#include "stdafx.h"
#include "pdewin.h"

#include "pdewinDoc.h"
#include "pdewinView.h"
#include "LeftView.h"
#include "TextDlg.h"
#include "DosFile.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

static const int nColExtraWidth(15);

/////////////////////////////////////////////////////////////////////////////
// CPdewinView

IMPLEMENT_DYNCREATE(CPdewinView, CListView)

BEGIN_MESSAGE_MAP(CPdewinView, CListView)
	//{{AFX_MSG_MAP(CPdewinView)
	ON_NOTIFY_REFLECT(LVN_GETDISPINFO, OnGetdispinfo)
	ON_NOTIFY_REFLECT(NM_DBLCLK, OnDblclk)
	ON_NOTIFY_REFLECT(LVN_BEGINDRAG, OnBegindrag)
	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()
	ON_MESSAGE(UM_DRAGGED_TO, OnDraggedTo)
	ON_MESSAGE(UM_DRAGGED_OFF, OnDraggedOff)
	ON_MESSAGE(UM_DROPPED_FILE, OnDroppedFile)
	ON_MESSAGE(UM_DROPPED_DIR, OnDroppedDir)
	ON_NOTIFY_REFLECT(LVN_KEYDOWN, OnKeydown)
	ON_NOTIFY_REFLECT(LVN_ENDLABELEDIT, OnEndlabeledit)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CPdewinView construction/destruction

CPdewinView::CPdewinView():
	m_pDragImage(NULL),
	m_bDragging(FALSE),
	m_nDropIndex(-1),
	m_hWndDraggedToPrev(NULL)
{
}

CPdewinView::~CPdewinView()
{
	delete m_pDragImage;
}

BOOL CPdewinView::PreCreateWindow(CREATESTRUCT& cs)
{
	cs.style |= LVS_REPORT | LVS_SINGLESEL | LVS_EDITLABELS;

	return CListView::PreCreateWindow(cs);
}

/////////////////////////////////////////////////////////////////////////////
// CPdewinView drawing

void CPdewinView::OnDraw(CDC* pDC)
{
}

void CPdewinView::OnInitialUpdate()
{
	CListView::OnInitialUpdate();

	m_imageList.Create(16,16,ILC_COLORDDB,2,8);
	m_imageList.Add((HICON)LoadImage(AfxGetInstanceHandle(),MAKEINTRESOURCE(IDI_FOLDER),IMAGE_ICON,16,16,LR_LOADTRANSPARENT));
	m_imageList.Add((HICON)LoadImage(AfxGetInstanceHandle(),MAKEINTRESOURCE(IDI_TEXT),IMAGE_ICON,16,16,LR_LOADTRANSPARENT));
	GetListCtrl().SetImageList(&m_imageList,LVSIL_SMALL);

//	GetListCtrl().SetExtendedStyle(LVS_EX_FULLROWSELECT);
}

/////////////////////////////////////////////////////////////////////////////
// CPdewinView diagnostics

#ifdef _DEBUG
void CPdewinView::AssertValid() const
{
	CListView::AssertValid();
}

void CPdewinView::Dump(CDumpContext& dc) const
{
	CListView::Dump(dc);
}

CPdewinDoc* CPdewinView::GetDocument() // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CPdewinDoc)));
	return (CPdewinDoc*)m_pDocument;
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CPdewinView message handlers
void CPdewinView::OnStyleChanged(int nStyleType, LPSTYLESTRUCT lpStyleStruct)
{
}

void CPdewinView::OnUpdate(CView* pSender, LPARAM lHint, CObject* pHint) 
{
	GetListCtrl().SetRedraw(FALSE);

	GetListCtrl().DeleteAllItems();
	while (GetListCtrl().DeleteColumn(0)) { }

	if (GetDocument()->m_osType==osProdos)
	{
		CString str = "File name";
		GetListCtrl().InsertColumn(0,str);
		CheckColumnWidth(0,str,TRUE);
		str = "Type";
		GetListCtrl().InsertColumn(1,str);
		CheckColumnWidth(1,str,TRUE);
		str = "Size";
		GetListCtrl().InsertColumn(2,str,LVCFMT_RIGHT);
		CheckColumnWidth(2,str,TRUE);
		str = "Created";
		GetListCtrl().InsertColumn(3,str);
		CheckColumnWidth(3,str,TRUE);
		str = "Modified";
		GetListCtrl().InsertColumn(4,str);
		CheckColumnWidth(4,str,TRUE);

		CProdosDir* pDir = GetDocument()->GetCurrentDir();
		if (pDir)
		{
			LVITEM item;
			for (int i(0); i<pDir->m_rpFile.GetSize(); i++)
			{
				item.iItem = i;
				item.pszText = LPSTR_TEXTCALLBACK;
				item.lParam = (DWORD)(pDir->m_rpFile[i]);

				item.mask = LVIF_TEXT|LVIF_PARAM|LVIF_IMAGE;
				item.iImage = pDir->m_rpFile[i]->m_nFileType==0x0F ? 0 : 1;
				item.iSubItem = 0;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString(pDir->m_rpFile[i],item.iSubItem));

				item.mask = LVIF_TEXT;
				item.iSubItem = 1;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString(pDir->m_rpFile[i],item.iSubItem));

				item.iSubItem = 2;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString(pDir->m_rpFile[i],item.iSubItem));

				item.iSubItem = 3;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString(pDir->m_rpFile[i],item.iSubItem));

				item.iSubItem = 4;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString(pDir->m_rpFile[i],item.iSubItem));
			}
		}
	}
	else
	{
		CString str = "File name";
		GetListCtrl().InsertColumn(0,str);
		CheckColumnWidth(0,str,TRUE);
		str = "Type";
		GetListCtrl().InsertColumn(1,str);
		CheckColumnWidth(1,str,TRUE);
		str = "Size";
		GetListCtrl().InsertColumn(2,str,LVCFMT_RIGHT);
		CheckColumnWidth(2,str,TRUE);

		CDosDir* pDir = GetDocument()->GetCurrentDir2();
		if (pDir)
		{
			LVITEM item;
			for (int i(0); i<pDir->m_rpFile.GetSize(); i++)
			{
				item.iItem = i;
				item.pszText = LPSTR_TEXTCALLBACK;
				item.lParam = (DWORD)(pDir->m_rpFile[i]);

				item.mask = LVIF_TEXT|LVIF_PARAM|LVIF_IMAGE;
				item.iImage = 1;
				item.iSubItem = 0;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString2(pDir->m_rpFile[i],item.iSubItem));

				item.mask = LVIF_TEXT;
				item.iSubItem = 1;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString2(pDir->m_rpFile[i],item.iSubItem));

				item.iSubItem = 2;
				GetListCtrl().InsertItem(&item);
				CheckColumnWidth(item.iSubItem,GetSubItemString2(pDir->m_rpFile[i],item.iSubItem));
			}
		}
	}

	GetListCtrl().SetRedraw();
}

void CPdewinView::CheckColumnWidth(int nCol, const CString& str, BOOL bForce)
{
	int nWidth = GetListCtrl().GetStringWidth(str)+nColExtraWidth;
	if (nCol==0)
		nWidth += 16; // image width
	if (bForce || nWidth>GetListCtrl().GetColumnWidth(nCol))
		GetListCtrl().SetColumnWidth(nCol,nWidth);
}

void CPdewinView::OnGetdispinfo(NMHDR* pNMHDR, LRESULT* pResult) 
{
	NMLVDISPINFO* pDispInfo = (NMLVDISPINFO*)pNMHDR;
	*pResult = 0;
	CString str;
	if (GetDocument()->m_osType==osProdos)
	{
		CProdosFile* pFile = (CProdosFile*)(pDispInfo->item.lParam);
		str = GetSubItemString(pFile,pDispInfo->item.iSubItem);
	}
	else
	{
		CDosFile* pFile = (CDosFile*)(pDispInfo->item.lParam);
		str = GetSubItemString2(pFile,pDispInfo->item.iSubItem);
	}
	strncpy(pDispInfo->item.pszText,(LPCTSTR)str,pDispInfo->item.cchTextMax);
}

CString CPdewinView::GetSubItemString(CProdosFile* pFile, int iSubItem)
{
	CString str;
	switch (iSubItem)
	{
	case 0:
		str = pFile->m_strName;
		CLeftView::FixCase(str);
		break;
	case 1:
		str = pFile->GetTypeString();
		break;
	case 2:
		str.Format("%d",pFile->m_nLen);
		break;
	case 3:
		str = pFile->m_pddateCreation.GetFormatted();
		break;
	case 4:
		str = pFile->m_pddateModification.GetFormatted();
		break;
	default:
		ASSERT(FALSE);
	}
	return str;
}

CString CPdewinView::GetSubItemString2(CDosFile* pFile, int iSubItem)
{
	CString str;
	switch (iSubItem)
	{
	case 0:
		str = pFile->m_strName;
		CLeftView::FixCase(str);
		break;
	case 1:
		str = pFile->GetTypeString();
		break;
	case 2:
		str.Format("%d",pFile->m_nLen);
		break;
	default:
		ASSERT(FALSE);
	}
	return str;
}

void CPdewinView::OnDblclk(NMHDR* pNMHDR, LRESULT* pResult) 
{
	*pResult = 0;
	POSITION pos = GetListCtrl().GetFirstSelectedItemPosition();
	while (pos)
	{
		int nItem = GetListCtrl().GetNextSelectedItem(pos);
		if (GetDocument()->m_osType==osProdos)
		{
			CProdosFile* pFile = GetItemFile(nItem);
			if (pFile->m_nFileType==0x04) // text
			{
				CTextDlg dlg(pFile->m_strContents);
				dlg.DoModal();
			}
			else if (pFile->m_nFileType==0x06||pFile->m_nFileType==0xFF) // binary or system
			{
				CTextDlg dlg(pFile->GetDisasm(),FALSE);
				dlg.DoModal();
			}
		}
		else
		{
			CDosFile* pFile = (CDosFile*)GetItemFile(nItem);
			if (pFile->m_nFileType==0x00) // text
			{
				CTextDlg dlg(pFile->m_strContents);
				dlg.DoModal();
			}
			else if (pFile->m_nFileType==0x04) // binary
			{
				CTextDlg dlg(pFile->GetDisasm(),FALSE);
				dlg.DoModal();
			}
		}
	}
}

void CPdewinView::OnBegindrag(NMHDR* pNMHDR, LRESULT* pResult) 
{
	NM_LISTVIEW* pNMListView = (NM_LISTVIEW*)pNMHDR;
	*pResult = 0;

	// this is the list item we're dragging
	m_nDragItem = pNMListView->iItem;
	//??? SelectItem

	// create a drag image
	delete m_pDragImage;
	CPoint pt(8,8);
	m_pDragImage = GetListCtrl().CreateDragImage(m_nDragItem,&pt);
	ASSERT (m_pDragImage);
	// changes the cursor to the drag image (DragMove() is still required in 
	// OnMouseMove())
	VERIFY(m_pDragImage->BeginDrag(0,CPoint(8, 8)));
	VERIFY(m_pDragImage->DragEnter(GetDesktopWindow(),pNMListView->ptAction));

	// set dragging flag
	m_bDragging = TRUE;
	m_nDropIndex = -1;

	// capture all mouse messages
	SetCapture();
}

void CPdewinView::OnLButtonUp(UINT nFlags, CPoint point) 
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
			::SendMessage(hWndDraggedTo,UM_DROPPED_FILE,GetDocument()->m_osType,(LPARAM)GetItemFile(m_nDragItem));
		}
	}
	CListView::OnLButtonUp(nFlags, point);
}

void CPdewinView::OnMouseMove(UINT nFlags, CPoint point) 
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
	CListView::OnMouseMove(nFlags, point);
}

CProdosFile* CPdewinView::GetItemFile(int nItem)
{
	return (CProdosFile*)(GetListCtrl().GetItemData(nItem));
}

void CPdewinView::OnDraggedTo(WPARAM wParam, LPARAM lParam)
{
	CPoint pt(LOWORD(lParam),HIWORD(lParam));
	GetListCtrl().ScreenToClient(&pt);

	UINT uFlags;
	int nCurDropIndex = GetListCtrl().HitTest(pt,&uFlags);

	if (nCurDropIndex!=m_nDropIndex)
	{
		HighlightDropItem(FALSE);
		m_nDropIndex = nCurDropIndex;
		HighlightDropItem(TRUE);
	}
}

void CPdewinView::OnDraggedOff(WPARAM wParam, LPARAM lParam)
{
	HighlightDropItem(FALSE);
}

void CPdewinView::HighlightDropItem(BOOL bHighlight)
{
	if (m_nDropIndex==-1)
		return;

	GetListCtrl().SetItemState(
		m_nDropIndex,
		bHighlight ? LVIS_DROPHILITED : 0,
		LVIS_DROPHILITED);

	GetListCtrl().RedrawItems(m_nDropIndex, m_nDropIndex);
	GetListCtrl().UpdateWindow();
}

void CPdewinView::OnDroppedFile(WPARAM wParam, LPARAM lParam)
{
	if (m_nDropIndex==-1)
		return;

	// user dropped one of our CProdosFiles (which may
	// represent an actual file, or a subdirectory) onto
	// one of our other CProdosFiles
	CProdosFile* pSourceFile = (CProdosFile*)lParam;
	CProdosFile* pDestFile = GetItemFile(m_nDropIndex);

	if (pSourceFile==pDestFile)
		return;

	if (GetDocument()->m_osType==osProdos && pDestFile->m_pPddir)
	{
		if ((osType)wParam==osProdos)
		{
			CString strSourceName = pSourceFile->m_strName;
			CLeftView::FixCase(strSourceName);
			CString strSource = pSourceFile->GetFullName();
			CString strDest = pDestFile->GetFullName()+"/"+strSourceName;
			CString str;
			str = "Do you want to copy the "+pSourceFile->GetTypeString()+" file:\n\n    "+strSource+"\n\nto:\n\n    "+strDest;
			if (AfxMessageBox(str,MB_YESNO)==IDNO)
				return;

			CProdosFile* pNewCopy = new CProdosFile(*pSourceFile);
			pNewCopy->m_pParent = pDestFile->m_pPddir;
			pDestFile->m_pPddir->m_rpFile.Add(pNewCopy);
		}
		else
		{
			CDosFile* pSourceFile2 = (CDosFile*)pSourceFile;
			CString strSourceName = pSourceFile2->m_strName;
			CLeftView::FixCase(strSourceName);
			CString strSource = pSourceFile2->GetFullName();
			CString strDest = pDestFile->GetFullName()+"/"+strSourceName;
			CString str;
			str = "Do you want to copy the "+pSourceFile->GetTypeString()+" file:\n\n    "+strSource+"\n\nto:\n\n    "+strDest;
			if (AfxMessageBox(str,MB_YESNO)==IDNO)
				return;

			CProdosFile* pNewCopy = new CProdosFile(*pSourceFile2);
			pNewCopy->m_pParent = pDestFile->m_pPddir;
			pDestFile->m_pPddir->m_rpFile.Add(pNewCopy);
		}
		GetDocument()->GetLeftView()->SetCurrentDir(pDestFile->m_pPddir);
		GetDocument()->SetModifiedFlag(TRUE);
		GetDocument()->UpdateWindow();
	}
	else
	{
		AfxMessageBox("You cannot drop an item onto a FILE.");
	}
}

void CPdewinView::OnDroppedDir(WPARAM wParam, LPARAM lParam)
{
	if (m_nDropIndex==-1)
		return;

	// user dropped a CProdosDir or CDosDir (from the left view) onto
	// one of our CProdosFiles or CDosFiles

	CProdosDir* pSourceDir = (CProdosDir*)lParam;
	CProdosFile* pDestFile = GetItemFile(m_nDropIndex);

	if (pSourceDir==NULL)
	{
		AfxMessageBox("You cannot drag that file. That feature has not been implemented.");
		return;
	}

	CProdosDir* pDestDirIfAny = NULL;
	if (GetDocument()->m_osType==osProdos)
	{
		pDestDirIfAny = pDestFile->m_pPddir;
		if (pDestDirIfAny==pSourceDir)
			return;
	}

	if (pDestDirIfAny)
	{
		if ((osType)wParam==osProdos)
		{
			CString strSourceName = pSourceDir->m_strName;
			CLeftView::FixCase(strSourceName);
			CString strSource = pSourceDir->GetFullName();
			CString strDest = pDestFile->GetFullName()+"/"+strSourceName;
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
			pNewCopy->m_pParent = pDestFile->m_pPddir;
			pDestFile->m_pPddir->m_rpFile.Add(pNewCopy);
		}
		else
		{
			CDosDir* pSourceDir2 = (CDosDir*)pSourceDir; //(entire DOS volume)
			CString strSourceName = pSourceDir2->m_strName;
			CLeftView::FixCase(strSourceName);
			CString strSource = pSourceDir2->GetFullName();
			CString strDest = pDestFile->GetFullName()+"/"+strSourceName;
			CString str;
			str = "Do you want to copy:\n\n    "+strSource+"\n\nto:\n\n    "+strDest;
			if (AfxMessageBox(str,MB_YESNO)==IDNO)
				return;

			CProdosFile* pNewCopy;
			// we need to construct a new CProdosFile (as well as a CProdosDir)
			pNewCopy = new CProdosFile(NULL);
			CProdosDir* pNewDir = new CProdosDir(*pSourceDir2);
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
			pNewCopy->m_pParent = pDestFile->m_pPddir;
			pDestFile->m_pPddir->m_rpFile.Add(pNewCopy);
		}

		GetDocument()->GetLeftView()->SetCurrentDir(pDestFile->m_pPddir);
		GetDocument()->SetModifiedFlag(TRUE);
		GetDocument()->UpdateWindow();
	}
	else
	{
		AfxMessageBox("You cannot drop an item onto a FILE.");
	}
}

void CPdewinView::OnKeydown(NMHDR* pNMHDR, LRESULT* pResult) 
{
	LV_KEYDOWN* pLVKeyDow = (LV_KEYDOWN*)pNMHDR;
	*pResult = 0;

	if (pLVKeyDow->wVKey==VK_DELETE)
	{
		DeleteCurrentItem();
	}
}

void CPdewinView::DeleteCurrentItem()
{
	if (AfxMessageBox("Are you sure you want to delete",MB_YESNO)==IDNO)
		return;

	POSITION pos = GetListCtrl().GetFirstSelectedItemPosition();
	while (pos)
	{
		int nItem = GetListCtrl().GetNextSelectedItem(pos);
		CProdosFile* pFile = GetItemFile(nItem);
		pFile->m_pParent->DeleteFile(pFile);
		GetDocument()->SetModifiedFlag(TRUE);
	}
	GetDocument()->UpdateWindow();
}

void CPdewinView::OnEndlabeledit(NMHDR* pNMHDR, LRESULT* pResult) 
{
	LV_DISPINFO* pDispInfo = (LV_DISPINFO*)pNMHDR;

	if (!pDispInfo->item.pszText) // if user cancelled the edit
		return;

	CProdosFile* pFile = (CProdosFile*)(pDispInfo->item.lParam);
	pFile->m_strName = pDispInfo->item.pszText;
	GetDocument()->SetModifiedFlag(TRUE);
	GetDocument()->UpdateWindow();

	// Call CListCtrl::SetItem, to force recalculation of the text length
	LV_ITEM lvi;
	lvi.mask = LVIF_TEXT;
	lvi.iItem = pDispInfo->item.iItem;
	lvi.iSubItem = 0;
	lvi.pszText = LPSTR_TEXTCALLBACK;
	GetListCtrl().SetItem(&lvi);

	*pResult = 0;
}
