// pdewinDoc.cpp : implementation of the CPdewinDoc class
//

#include "stdafx.h"
#include "pdewin.h"

#include "pdewinDoc.h"
#include "LeftView.h"
#include "PdewinView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CPdewinDoc

IMPLEMENT_DYNCREATE(CPdewinDoc, CDocument)

BEGIN_MESSAGE_MAP(CPdewinDoc, CDocument)
	//{{AFX_MSG_MAP(CPdewinDoc)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CPdewinDoc construction/destruction

CPdewinDoc::CPdewinDoc():
	m_pRawImage(NULL)
{
}

CPdewinDoc::~CPdewinDoc()
{
	delete m_pRawImage;
}

BOOL CPdewinDoc::OnNewDocument()
{
	if (!CDocument::OnNewDocument())
		return FALSE;

	return TRUE;
}

void CPdewinDoc::UpdateWindow()
{
	UpdateAllViews(GetRightView());
}

/////////////////////////////////////////////////////////////////////////////
// CPdewinDoc serialization

void CPdewinDoc::Serialize(CArchive& ar)
{
	if (ar.IsStoring())
	{
		ar.Write(m_pRawImage,m_stat.m_size);
	}
	else
	{
		ar.GetFile()->GetStatus(m_stat);
/*
		if (m_stat.m_size != 0x118*0x200)
		{
			if (AfxMessageBox("That file doesn't appear to be a 16-sector disk image. "
				"Do you want to try to open it anyway?",MB_YESNO)==IDNO)
				AfxThrowUserException();
		}
*/
		m_pRawImage = new BYTE[m_stat.m_size];
		UINT cActual = ar.Read(m_pRawImage,m_stat.m_size);
		m_rBlock = (block*)m_pRawImage;
		m_rTrackSector = (track*)m_pRawImage;
		ParseImage();
		delete m_pRawImage;
		m_pRawImage = NULL;
	}
}

/////////////////////////////////////////////////////////////////////////////
// CPdewinDoc diagnostics

#ifdef _DEBUG
void CPdewinDoc::AssertValid() const
{
	CDocument::AssertValid();
}

void CPdewinDoc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
	dc << "Document dump:\n";
	dc << "OS Type==" << (long)m_osType << "\n";
	dc << "Volume dir:\n";
	m_dir.Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CPdewinDoc commands

void CPdewinDoc::ParseImage()
{
	FixOrder();

	if (m_osType==osProdos)
	{
		GetLeftView()->SetCurrentDir(&m_dir);
		m_dir.Parse(m_rBlock,2);
	}
	else
	{
		GetLeftView()->SetCurrentDir((CProdosDir*)&m_dir2);
		m_dir2.Parse(m_rTrackSector,ts(0x11,0));
	}
}

void CPdewinDoc::FixOrder()
{
	// see if this .DSK image is in block-order
	// or track-sector-order.
	if (
		m_rTrackSector[0][0xB][0]==0 &&
		m_rTrackSector[0][0xB][1]==0 &&
		m_rTrackSector[0][0xB][2]==3 &&
		m_rTrackSector[0][0xB][3]==0 &&
		m_rTrackSector[0][0xB][4]>>4==0xF)
	{
		m_osType = osProdos;
		// Prodos disk in track-sector order
		// switch to block order
		SwitchTSBlock();
	}
	else if (
		m_rBlock[2][0]==0 &&
		m_rBlock[2][1]==0 &&
		m_rBlock[2][2]==3 &&
		m_rBlock[2][3]==0 &&
		m_rBlock[2][4]>>4==0xF)
	{
		m_osType = osProdos;
		// Prodos disk in block order
		// OK
	}
	else if (
		m_rTrackSector[0x11][0xE][1]==0x11 &&
		m_rTrackSector[0x11][0xE][2]==0x0D)
	{
		m_osType = osDos33;
		// DOS 3.3 disk in track-sector order
		// OK
	}
	else if (
		m_rBlock[0x88][0x101]==0x11 &&
		m_rBlock[0x88][0x102]==0x0D)
	{
		m_osType = osDos33;
		// DOS 3.3 disk in block order
		// switch to track-sector order
		SwitchTSBlock();
	}
	else
	{
		AfxMessageBox("Cannot determine type of disk.");
		AfxThrowUserException();
	}
}

void CPdewinDoc::SwitchTSBlock()
{
	sector sc;
	for (int itr(0); itr<0x23; itr++)
	{
		for (int isc(1); isc<8; isc++)
		{
			int iscSwap = 0xF-isc;
			memcpy(sc,m_rTrackSector[itr][isc],sizeof(sector));
			memcpy(m_rTrackSector[itr][isc],m_rTrackSector[itr][iscSwap],sizeof(sector));
			memcpy(m_rTrackSector[itr][iscSwap],sc,sizeof(sector));
		}
	}
}

CLeftView* CPdewinDoc::GetLeftView()
{
	POSITION pos = GetFirstViewPosition();
	CLeftView* pLeftView = DYNAMIC_DOWNCAST(CLeftView,GetNextView(pos));
	return pLeftView;
}

CPdewinView* CPdewinDoc::GetRightView()
{
	POSITION pos = GetFirstViewPosition();
	CLeftView* pLeftView = DYNAMIC_DOWNCAST(CLeftView,GetNextView(pos));
	CPdewinView* pRightView = DYNAMIC_DOWNCAST(CPdewinView,GetNextView(pos));
	return pRightView;
}

CProdosDir* CPdewinDoc::GetCurrentDir()
{
	return GetLeftView()->GetCurrentDir();
}

CDosDir* CPdewinDoc::GetCurrentDir2()
{
	return GetLeftView()->GetCurrentDir2();
}
