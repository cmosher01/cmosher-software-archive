// ProdosDir.cpp : implementation file
//

#include "stdafx.h"
#include "pdewin.h"
#include "ProdosDir.h"
#include "DosDir.h"
#include "LeftView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CProdosDir

CProdosDir::CProdosDir(CProdosFile* pParent):
	m_pParent(pParent)
{
}

CProdosDir::~CProdosDir()
{
	DeleteAllFiles();
}

void CProdosDir::DeleteAllFiles()
{
	for (int i(0); i<m_rpFile.GetSize(); i++)
		delete m_rpFile[i];
	m_rpFile.RemoveAll();
}

void CProdosDir::DeleteFile(CProdosFile* pFile)
{
	int iDelete(-1);
	for (int i(0); iDelete<0 && i<m_rpFile.GetSize(); i++)
		if (m_rpFile[i]==pFile)
			iDelete = i;

	ASSERT(!(iDelete<0));
	delete pFile;
	m_rpFile.RemoveAt(iDelete);
}

/////////////////////////////////////////////////////////////////////////////
// CProdosDir diagnostics

#ifdef _DEBUG
void CProdosDir::AssertValid() const
{
}

void CProdosDir::Dump(CDumpContext& dc) const
{
	dc << "CProdosDir (" << (long)this << "): --------------------------------------------------------"
		<< m_strName << "\n";
	dc << "IsVolDir==" << m_bIsVolDir << "\n";
	dc << "Parent ptr==" << (long)m_pParent << "\n";
	dc << "dateCreation==" << m_pddateCreation.GetFormatted() << "\n";
	dc << "m_nVersion==" << m_nVersion << "\n";
	dc << "m_nMinVersion==" << m_nMinVersion << "\n";
	dc << "m_nCanDestroy==" << m_nCanDestroy << "\n";
	dc << "m_nCanRename==" << m_nCanRename << "\n";
	dc << "m_nCanBackup==" << m_nCanBackup << "\n";
	dc << "m_nCanWrite==" << m_nCanWrite << "\n";
	dc << "m_nCanRead==" << m_nCanRead << "\n";
	dc << "m_nEntryLength==" << m_nEntryLength << "\n";
	dc << "m_nEntriesPerBlock==" << m_nEntriesPerBlock << "\n";
	dc << m_rpFile.GetSize() << " files:\n";
	dc.Flush();
	for (int i(0); i<m_rpFile.GetSize(); i++)
		m_rpFile[i]->Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CProdosDir

void CProdosDir::Parse(block rBlock[], int iStart)
{
	int iBlock(iStart);
	m_bHasDirHeader = TRUE;
	while (iBlock)
	{
		ParseEntries(rBlock,&rBlock[iBlock][4]);
		m_bHasDirHeader = FALSE;
		iBlock = *(short*)&rBlock[iBlock][2]; // next block num
	}
}

void CProdosDir::ParseEntries(block rBlock[], BYTE* start)
{
	int nFileCount;

	if (m_bHasDirHeader)
	{
		if (*start>>4==0xF)
			m_bIsVolDir = TRUE;
		else if (*start>>4==0xE)
			m_bIsVolDir = FALSE;
		else
			ASSERT(FALSE);

		int nLen = *start++ & 0x0F; // get len, adv to name
		m_strName = CString((LPTSTR)start,nLen);

		start += 15; // adv past name

		start += 8; // adv past reserved

		m_pddateCreation.Set(*(long*)start);
		start += 4;

		m_nVersion = *start++;
		m_nMinVersion = *start++;

		m_nCanDestroy = *start & 0x80;
		m_nCanRename  = *start & 0x40;
		m_nCanBackup  = *start & 0x20;
		m_nCanWrite   = *start & 0x02;
		m_nCanRead    = *start & 0x01;
		start++;

		m_nEntryLength = *start++;
		m_nEntriesPerBlock = *start++;

		start += 6; // skip this stuff for now

		nFileCount = m_nEntriesPerBlock-1;
	}
	else
	{
		nFileCount = m_nEntriesPerBlock;
	}

	//DeleteAllFiles(); why does this not work???
	for (int i(0); i<nFileCount; i++)
	{
		CProdosFile* pf = new CProdosFile(this);
		pf->Parse(rBlock,start);
		if (!pf->m_bDeleted)
			m_rpFile.Add(pf);
		else
			delete pf;
		start += m_nEntryLength;
	}
}

CString CProdosDir::GetFullName()
{
	CString strPath;

	CString str = m_strName;
	CLeftView::FixCase(str);
	strPath = str;

	CProdosFile* pFile = m_pParent;
	while (pFile)
	{
		CProdosDir* pDir = pFile->m_pParent;
		str = pDir->m_strName;
		CLeftView::FixCase(str);
		strPath = str+"/"+strPath;

		pFile = pDir->m_pParent;
	}

	return "/"+strPath;
}

CProdosDir::CProdosDir(const CProdosDir& o):
	m_pParent(o.m_pParent),
	m_bIsVolDir(o.m_bIsVolDir),
	m_strName(o.m_strName),
	m_pddateCreation(o.m_pddateCreation),
	m_nVersion(o.m_nVersion),
	m_nMinVersion(o.m_nMinVersion),
	m_nCanDestroy(o.m_nCanDestroy),
	m_nCanRename(o.m_nCanRename),
	m_nCanBackup(o.m_nCanBackup),
	m_nCanWrite(o.m_nCanWrite),
	m_nCanRead(o.m_nCanRead),
	m_nEntryLength(o.m_nEntryLength),
	m_nEntriesPerBlock(o.m_nEntriesPerBlock),
	m_bHasDirHeader(o.m_bHasDirHeader)
{
	for (int i(0); i<o.m_rpFile.GetSize(); i++)
	{
		CProdosFile* pf = new CProdosFile(*o.m_rpFile[i]);
		pf->m_pParent = this;
		m_rpFile.Add(pf);
	}
}

CProdosDir& CProdosDir::operator=(const CProdosDir& o)
{
	if (this == &o) return *this;
	m_pParent = o.m_pParent;
	m_bIsVolDir = o.m_bIsVolDir;
	m_strName = o.m_strName;
	m_pddateCreation = o.m_pddateCreation;
	m_nVersion = o.m_nVersion;
	m_nMinVersion = o.m_nMinVersion;
	m_nCanDestroy = o.m_nCanDestroy;
	m_nCanRename = o.m_nCanRename;
	m_nCanBackup = o.m_nCanBackup;
	m_nCanWrite = o.m_nCanWrite;
	m_nCanRead = o.m_nCanRead;
	m_nEntryLength = o.m_nEntryLength;
	m_nEntriesPerBlock = o.m_nEntriesPerBlock;
	m_bHasDirHeader = o.m_bHasDirHeader;
	for (int i(0); i<o.m_rpFile.GetSize(); i++)
	{
		CProdosFile* pf = new CProdosFile(*o.m_rpFile[i]);
		pf->m_pParent = this;
		m_rpFile.Add(pf);
	}

	return *this;
}

CProdosDir::CProdosDir(const CDosDir& o):
	m_pParent(NULL),
	m_bIsVolDir(FALSE),
	m_strName(o.m_strName),
	m_nVersion(0),
	m_nMinVersion(0),
	m_nCanDestroy(TRUE),
	m_nCanRename(TRUE),
	m_nCanBackup(TRUE),
	m_nCanWrite(TRUE),
	m_nCanRead(TRUE),
	m_nEntryLength(0x27),
	m_nEntriesPerBlock(0x0D),
	m_bHasDirHeader(TRUE)
{
	for (int i(0); i<o.m_rpFile.GetSize(); i++)
	{
		CProdosFile* pf = new CProdosFile(*o.m_rpFile[i]);
		pf->m_pParent = this;
		m_rpFile.Add(pf);
	}
}

int CProdosDir::CatalogBlocks()
{
	return CeilDiv(1+m_rpFile.GetSize(),m_nEntriesPerBlock);
}

int CProdosDir::TotalBlocks(BOOL bRecurse)
{
	int nTotal(0);
	for (int i(0); i<m_rpFile.GetSize(); i++)
	{
		nTotal += m_rpFile[i]->TotalBlocks(bRecurse);
	}
	return nTotal + CatalogBlocks();
}

int CProdosDir::FreeMapBlocks()
{
	ASSERT(m_bIsVolDir);

	return 1; //??? need total blocks on disk to calc this
}
