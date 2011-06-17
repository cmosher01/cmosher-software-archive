#if !defined(AFX_PRODOSFILE_H__2C420697_26C6_11D3_9A03_204C4F4F5020__INCLUDED_)
#define AFX_PRODOSFILE_H__2C420697_26C6_11D3_9A03_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// ProdosFile.h : header file
//

class CProdosDir;
class CDosFile;
#include "ProdosDate.h"

/////////////////////////////////////////////////////////////////////////////
// CProdosFile

class CProdosFile
{
// Construction
public:
	CProdosFile(CProdosDir* pParent);
	CProdosFile(const CProdosFile& o);
	CProdosFile& operator=(const CProdosFile& o);
	CProdosFile(const CDosFile& o);
	virtual ~CProdosFile();

// Attributes
public:
	CProdosDir* m_pParent;
	BOOL m_bDeleted;
	CString m_strName;
	CString GetTypeString();
	int m_nFileType;
	int m_nLen;
	CProdosDate m_pddateCreation;
	int m_nVersion;
	int m_nMinVersion;
	BOOL m_nCanDestroy;
	BOOL m_nCanRename;
	BOOL m_nCanBackup;
	BOOL m_nCanWrite;
	BOOL m_nCanRead;
	int m_nAuxType;
	CProdosDate m_pddateModification;
	CString m_strContents;

	CProdosDir* m_pPddir;

	CString GetFullName();
	CString GetDisasm();
	static CString disasm(int nBaseAddress, LPCTSTR rBuffer, int nBufferSize);

	int DataBlocks();
	int KeyBlocks();
	int TotalBlocks(BOOL bRecurse = TRUE);

// Operations
public:
	void Parse(block rBlock[], BYTE* start);
	CString ReadFileBlock(block rBlock[], int nKey, int nLen);
	void ReadKeyBlock(block rBlock[], int nKey, CArray<short,short>& rFileBlock);
	short NthBlockInKey(block nBlock, int n);

// Implementation
public:
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif
};

/////////////////////////////////////////////////////////////////////////////

#endif // !defined(AFX_PRODOSFILE_H__2C420697_26C6_11D3_9A03_204C4F4F5020__INCLUDED_)
