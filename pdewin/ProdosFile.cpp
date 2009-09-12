// ProdosFile.cpp : implementation file
//

#include "stdafx.h"
#include "pdewin.h"
#include "ProdosFile.h"
#include "ProdosDir.h"
#include "DosFile.h"
#include "LeftView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CProdosFile

CProdosFile::CProdosFile(CProdosDir* pParent):
	m_pParent(pParent),
	m_pPddir(NULL)
{
}

CProdosFile::~CProdosFile()
{
	delete m_pPddir;
}
/////////////////////////////////////////////////////////////////////////////
// CProdosDir diagnostics

#ifdef _DEBUG
void CProdosFile::AssertValid() const
{
}

void CProdosFile::Dump(CDumpContext& dc) const
{
	dc << "CProdosFile (" << (long)this << "): --------------------------------------------------------"
		<< m_strName << "\n";
	dc << "m_nFileType==" << m_nFileType << "\n";
	dc << "m_nLen==" << m_nLen << "\n";
	dc << "Parent ptr==" << (long)m_pParent << "\n";
	dc << "dateCreation==" << (LPCTSTR)m_pddateCreation.GetFormatted() << "\n";
	dc << "m_pddateModification==" << (LPCTSTR)m_pddateModification.GetFormatted() << "\n";
	dc << "m_nVersion==" << m_nVersion << "\n";
	dc << "m_nMinVersion==" << m_nMinVersion << "\n";
	dc << "m_nCanDestroy==" << m_nCanDestroy << "\n";
	dc << "m_nCanRename==" << m_nCanRename << "\n";
	dc << "m_nCanBackup==" << m_nCanBackup << "\n";
	dc << "m_nCanWrite==" << m_nCanWrite << "\n";
	dc << "m_nCanRead==" << m_nCanRead << "\n";
	dc << "m_nAuxType==" << m_nAuxType << "\n";
	dc.Flush();
	if (m_pPddir)
	{
		dc << "Is a directory, m_pPddir: " << (long)m_pPddir << "\n";
		m_pPddir->Dump(dc);
	}
}
#endif //_DEBUG

void CProdosFile::Parse(block rBlock[], BYTE* start)
{
	int nStorageType = *start>>4;
	m_bDeleted = !nStorageType;

	if (m_bDeleted)
		return;

	int nLen = *start++ & 0x0F; // get len, adv to name
	m_strName = CString((LPTSTR)start,nLen);

	start += 15; // adv past name

	m_nFileType = *start++;

	short nKey = *(short*)start;
	start += 2;

	start += 2; // skip blocks in use for now

	m_nLen = *(long*)start & 0x00FFFFFF;
	start += 3;

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

	m_nAuxType = *start;
	start += 2;
	m_pddateModification.Set(*(long*)start);
	start += 4;

	start += 2; // skip dir head block for now

	// if file read in contents of file
	// if sub dir, read in that sub dir

	if (nStorageType==0xD)
	{
		m_pPddir = new CProdosDir(this);
		m_pPddir->Parse(rBlock,nKey);
		// copy file-header info to dir-header info
		// (from this to m_pPddir):
		// name, creation date, vers, min vers, access
		// these pieces of data are redundant, and the ones
		// in the dir-header are ignored by ProDOS, except
		// that the dir-header's NAME is displayed only when
		// a dir is done of that sub dir (but not of the parent dir)
		// we ignore this fact, and for consistnecy always use same
		// info (from file-header)
		m_pPddir->m_strName = m_strName;
		m_pPddir->m_pddateCreation = m_pddateCreation;
		m_pPddir->m_nVersion = m_nVersion;
		m_pPddir->m_nMinVersion = m_nMinVersion;
		m_pPddir->m_nCanDestroy = m_nCanDestroy;
		m_pPddir->m_nCanRename  = m_nCanRename;
		m_pPddir->m_nCanBackup  = m_nCanBackup;
		m_pPddir->m_nCanWrite   = m_nCanWrite;
		m_pPddir->m_nCanRead    = m_nCanRead;
	}
	else
	{
		if (nStorageType==1)
		{
			m_strContents = ReadFileBlock(rBlock,nKey,m_nLen);
		}
		else if (nStorageType==2)
		{
			CArray<short,short> m_rFileBlock;
			ReadKeyBlock(rBlock,nKey,m_rFileBlock);
			int nLen = m_nLen;
			for (int i(0); i<m_rFileBlock.GetSize(); i++)
			{
				int nLenCur = min(sizeof block,nLen);
				m_strContents += ReadFileBlock(rBlock,m_rFileBlock[i],nLenCur);
				nLen -= sizeof block;
			}
		}
		else if (nStorageType==3)
		{
			AfxMessageBox("Tree files not yet working.");
		}
	}
}

CString CProdosFile::ReadFileBlock(block rBlock[], int nKey, int nLen)
{
	return CString((LPCTSTR)rBlock[nKey],nLen);
}

void CProdosFile::ReadKeyBlock(block rBlock[], int nKey, CArray<short,short>& rFileBlock)
{
	int i(0);
	short blk = NthBlockInKey(rBlock[nKey],i++);
	while (blk)
	{
		rFileBlock.Add(blk);
		blk = NthBlockInKey(rBlock[nKey],i++);
	}
}

short CProdosFile::NthBlockInKey(block nBlock, int n)
{
	return (nBlock[n+0x100]<<8) + (nBlock[n]);
}

CString CProdosFile::GetFullName()
{
	CString strPath;

	CString str = m_strName;
	CLeftView::FixCase(str);
	strPath = str;

	CProdosDir* pDir = m_pParent;
	str = pDir->m_strName;
	CLeftView::FixCase(str);
	strPath = str+"/"+strPath;

	CProdosFile* pFile = pDir->m_pParent;
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

CString CProdosFile::GetTypeString()
{
	CString str;
	switch (m_nFileType)
	{
	case 0x04:
		str = "Text";
		break;
	case 0x06:
		str = "Binary";
		break;
	case 0x0F:
		str = "Directory";
		break;
	case 0xFC:
		str = "BASIC";
		break;
	case 0xFD:
		str = "BASIC Var";
		break;
	case 0xFF:
		str = "System";
		break;
	default:
		str.Format("$%2.2X",m_nFileType);
	}
	return str;
}

CString CProdosFile::GetDisasm()
{
	ASSERT(m_nFileType==0x06||m_nFileType==0xFF);
	int nBaseAddress = m_nAuxType;
	if (m_nFileType==0xFF)
		nBaseAddress = 0x2000;
	return disasm(nBaseAddress,m_strContents,m_strContents.GetLength());
}

static CString rsFormat[0x100];
static int rnArgs[0x100];

static void fill()
{
#define inst(x,format,y,byte,nargs) rsFormat[byte] = format; rnArgs[byte] = nargs;
#include "instr.h"
}

CString CProdosFile::disasm(int nBaseAddress, LPCTSTR rBufferAddr, int nBufferSize)
{
	fill();

	BYTE *rBuffer = (BYTE*)rBufferAddr;
	CString sProg;
	int pc(nBaseAddress);
	int i(0);
	while (i<nBufferSize)
	{
		CString s;
		int n = rBuffer[i];
		int nargs = rnArgs[n];
		if (nargs==1)
			s.Format("%4.4X\t"+rsFormat[n]+"\r\n",pc,rBuffer[i+1]);
		else if (nargs==2)
			s.Format("%4.4X\t"+rsFormat[n]+"\r\n",pc,rBuffer[i+2],rBuffer[i+1]);
		else if (nargs==0)
			s.Format("%4.4X\t"+rsFormat[n]+"\r\n",pc);
		else if (nargs==3)
		{
			nargs = 1;
			char off = (char)rBuffer[i+1];
			ASSERT(-0x80<=off && off<0x80);
			s.Format("%4.4X\t"+rsFormat[n]+"\r\n",pc,pc+nargs+1+off,rBuffer[i+1]);
		}
		else
			ASSERT(FALSE);

		sProg += s;
		pc += nargs+1; i += nargs+1;
	}
	return sProg;
}

CProdosFile::CProdosFile(const CProdosFile& o):
	m_pParent(o.m_pParent),
	m_bDeleted(o.m_bDeleted),
	m_strName(o.m_strName),
	m_nFileType(o.m_nFileType),
	m_nLen(o.m_nLen),
	m_pddateCreation(o.m_pddateCreation),
	m_nVersion(o.m_nVersion),
	m_nMinVersion(o.m_nMinVersion),
	m_nCanDestroy(o.m_nCanDestroy),
	m_nCanRename(o.m_nCanRename),
	m_nCanBackup(o.m_nCanBackup),
	m_nCanWrite(o.m_nCanWrite),
	m_nCanRead(o.m_nCanRead),
	m_nAuxType(o.m_nAuxType),
	m_pddateModification(o.m_pddateModification),
	m_strContents(o.m_strContents)
{
	if (o.m_pPddir)
	{
		m_pPddir = new CProdosDir(*o.m_pPddir);
		m_pPddir->m_pParent = this;
	}
	else
	{
		m_pPddir = NULL;
	}
}

CProdosFile& CProdosFile::operator=(const CProdosFile& o)
{
	if (this == &o) return *this;
	m_pParent = o.m_pParent;
	m_bDeleted = o.m_bDeleted;
	m_strName = o.m_strName;
	m_nFileType = o.m_nFileType;
	m_nLen = o.m_nLen;
	m_pddateCreation = o.m_pddateCreation;
	m_nVersion = o.m_nVersion;
	m_nMinVersion = o.m_nMinVersion;
	m_nCanDestroy = o.m_nCanDestroy;
	m_nCanRename = o.m_nCanRename;
	m_nCanBackup = o.m_nCanBackup;
	m_nCanWrite = o.m_nCanWrite;
	m_nCanRead = o.m_nCanRead;
	m_nAuxType = o.m_nAuxType;
	m_pddateModification = o.m_pddateModification;
	m_strContents = o.m_strContents;
	if (o.m_pPddir)
	{
		m_pPddir = new CProdosDir(*o.m_pPddir);
		m_pPddir->m_pParent = this;
	}
	else
	{
		m_pPddir = NULL;
	}

	return *this;
}

CProdosFile::CProdosFile(const CDosFile& o):
	m_pParent(NULL),
	m_bDeleted(o.m_bDeleted),
	m_strName(o.m_strName),
	m_nLen(o.m_nLen),
	m_nVersion(0),
	m_nMinVersion(0),
	m_nCanDestroy(o.m_bLocked),
	m_nCanRename(o.m_bLocked),
	m_nCanBackup(TRUE),
	m_nCanWrite(o.m_bLocked),
	m_nCanRead(TRUE),
	m_nAuxType(o.m_nAuxType),
	m_strContents(o.m_strContents)
{
	switch (o.m_nFileType)
	{
	case 0x00:
		m_nFileType = 0x04; //Text
		break;
	case 0x01:
		m_nFileType = 0x06; //Integer BASIC -> binary
		break;
	case 0x02:
		m_nFileType = 0xFC; //BASIC
		break;
	case 0x04:
		m_nFileType = 0x06; //Binary
		break;
	case 0x08:
		m_nFileType = 0x06; //Special -> binary
		break;
	case 0x10:
		m_nFileType = 0x06; //Relocatable -> binary
		break;
	case 0x20:
		m_nFileType = 0xFC; //BASIC (alt) -> BASIC
		break;
	case 0x40:
		m_nFileType = 0x06; //Binary (alt) -> binary
		break;
	default:
		m_nFileType = 0x06; //default to binary
	}

	m_pPddir = NULL;
}

int CProdosFile::DataBlocks()
{
	int nBytes = m_strName.GetLength();

	// special case (empty files)
	// if no bytes, still we use up one block
	if (!nBytes)
		return 1;

	return CeilDiv(nBytes,BYTES_PER_BLOCK);
}

int CProdosFile::KeyBlocks()
{
	// special case (seedling files)
	// if only one data block is needed, then
	// we don't need a key block at all
	if (DataBlocks()<=1)
		return 0;

	return CeilDiv(DataBlocks(),BYTES_PER_BLOCK/2);
}

int CProdosFile::TotalBlocks(BOOL bRecurse)
{
	if (m_pPddir)
	{
		if (bRecurse)
			return m_pPddir->TotalBlocks(bRecurse);
		else
			return 0;
	}
	else
	{
		return KeyBlocks()+DataBlocks();
	}
}
