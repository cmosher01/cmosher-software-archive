// pdewinDoc.h : interface of the CPdewinDoc class
//
/////////////////////////////////////////////////////////////////////////////

#if !defined(AFX_PDEWINDOC_H__56A0508E_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
#define AFX_PDEWINDOC_H__56A0508E_25F6_11D3_9A02_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ProdosDir.h"
#include "DosDir.h"
class CLeftView;
class CPdewinView;

class CPdewinDoc : public CDocument
{
protected: // create from serialization only
	CPdewinDoc();
	DECLARE_DYNCREATE(CPdewinDoc)

// Attributes
public:
	osType m_osType;
	CProdosDir m_dir;
	CDosDir m_dir2;
protected:
	BYTE* m_pRawImage;
	CFileStatus m_stat;
	block *m_rBlock;
	track *m_rTrackSector;

// Operations
public:
	CProdosDir* GetCurrentDir();
	CDosDir* GetCurrentDir2();
	CLeftView* GetLeftView();
	CPdewinView* GetRightView();
	void UpdateWindow();

protected:
	void ParseImage();
	void FixOrder();
	void SwitchTSBlock();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CPdewinDoc)
	public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CPdewinDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	//{{AFX_MSG(CPdewinDoc)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_PDEWINDOC_H__56A0508E_25F6_11D3_9A02_204C4F4F5020__INCLUDED_)
