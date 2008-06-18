#if !defined(AFX_PRODOSDIR_H__2C420696_26C6_11D3_9A03_204C4F4F5020__INCLUDED_)
#define AFX_PRODOSDIR_H__2C420696_26C6_11D3_9A03_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// ProdosDir.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CProdosDir

#include "ProdosDate.h"
#include "ProdosFile.h"
class CDosDir;

class CProdosDir
{
public:
	CProdosDir(CProdosFile* pParent = NULL);
	CProdosDir(const CProdosDir& o);
	CProdosDir& operator=(const CProdosDir& o);
	CProdosDir(const CDosDir& o);
	virtual ~CProdosDir();

// Attributes
public:
	CProdosFile* m_pParent;
	BOOL m_bIsVolDir; // T->vol dir, F->sub dir
	CString m_strName;
	CProdosDate m_pddateCreation;
	int m_nVersion;
	int m_nMinVersion;
	BOOL m_nCanDestroy;
	BOOL m_nCanRename;
	BOOL m_nCanBackup;
	BOOL m_nCanWrite;
	BOOL m_nCanRead;
	int m_nEntryLength;
	int m_nEntriesPerBlock;
	CArray<CProdosFile*,CProdosFile*> m_rpFile;

	BOOL m_bHasDirHeader;
	CString GetFullName();

	int CatalogBlocks();
	int TotalBlocks(BOOL bRecurse = TRUE);
	int FreeMapBlocks();

// Operations
public:
	void Parse(block rBlock[], int iStart);
	void ParseEntries(block rBlock[], BYTE* start);
	void DeleteAllFiles();
	void DeleteFile(CProdosFile* pFile);

// Implementation
public:
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif
};

/////////////////////////////////////////////////////////////////////////////

#endif // !defined(AFX_PRODOSDIR_H__2C420696_26C6_11D3_9A03_204C4F4F5020__INCLUDED_)
