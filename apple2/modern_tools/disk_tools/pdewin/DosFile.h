#if !defined(AFX_DOSFILE_H__10E3B371_2B7B_11D3_9A0B_204C4F4F5020__INCLUDED_)
#define AFX_DOSFILE_H__10E3B371_2B7B_11D3_9A0B_204C4F4F5020__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// DosFile.h : header file
//

class CDosDir;
class CProdosFile;

/////////////////////////////////////////////////////////////////////////////
// CDosFile

class CDosFile
{
// Construction
public:
	CDosFile(CDosDir* pParent);
	CDosFile(const CDosFile& o);
	CDosFile& operator=(const CDosFile& o);
	CDosFile(const CProdosFile& o);
	virtual ~CDosFile();

// Attributes
public:
	CDosDir* m_pParent;
	BOOL m_bLocked;
	BOOL m_bDeleted;
	CString m_strName;
	CString GetTypeString();
	int m_nFileType;
	int m_nLen;
	int m_nAuxType;
	CString m_strContents;

	CString GetFullName();
	CString GetDisasm();
	static CString disasm(int nBaseAddress, LPCTSTR rBuffer, int nBufferSize);

// Operations
public:
	void Parse(track rTSB[], int t, int s, int b);
	CString ReadFileBlock(block rBlock[], int nKey, int nLen);

// Implementation
public:
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif
protected:
	CString ReadFileSector(track rTSB[], int t, int s);
	void FixDosName();
};

/////////////////////////////////////////////////////////////////////////////

#endif // !defined(AFX_DOSFILE_H__10E3B371_2B7B_11D3_9A0B_204C4F4F5020__INCLUDED_)
