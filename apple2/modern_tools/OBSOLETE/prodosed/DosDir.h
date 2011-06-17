#if !defined(AFX_DOSDIR_H__10E3B374_2B7B_11D3_9A0B_204C4F4F5020__INCLUDED_)
#define AFX_DOSDIR_H__10E3B374_2B7B_11D3_9A0B_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// DosDir.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CDosDir

class CDosFile;
class CProdosDir;

struct ts
{
	BYTE track;
	BYTE sector;
	ts(BYTE t = 0, BYTE s = 0):
		track(t), sector(s) { }
	operator BOOL() { return track && sector; }
};

class CDosDir
{
public:
	CDosDir();
	CDosDir(const CDosDir& o);
	CDosDir& operator=(const CDosDir& o);
	CDosDir(const CProdosDir& o);
	virtual ~CDosDir();

// Attributes
public:
	CString m_strName;
	int m_nDiskVolume;
	ts m_tsCatalog;

	CArray<CDosFile*,CDosFile*> m_rpFile;

	CString GetFullName();

	int CatalogBlocks();
	int TotalBlocks(BOOL bRecurse = TRUE);
	int FreeMapBlocks();

// Operations
public:
	void Parse(track rTSB[], ts tsCat);
	void ParseEntries(track rTSB[]);
	void DeleteAllFiles();
	void DeleteFile(CDosFile* pFile);

// Implementation
public:
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif
protected:
	void NextCat(track rTSB[]);
};

/////////////////////////////////////////////////////////////////////////////

#endif // !defined(AFX_DOSDIR_H__10E3B374_2B7B_11D3_9A0B_204C4F4F5020__INCLUDED_)
