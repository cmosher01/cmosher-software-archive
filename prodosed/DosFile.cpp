// DosFile.cpp : implementation file
//

#include "stdafx.h"
#include "pdewin.h"
#include "DosFile.h"
#include "ProdosFile.h"
#include "DosDir.h"
#include "LeftView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CDosFile

CDosFile::CDosFile(CDosDir* pParent):
	m_pParent(pParent)
{
}

CDosFile::~CDosFile()
{
}

/////////////////////////////////////////////////////////////////////////////
// CDosDir diagnostics

#ifdef _DEBUG
void CDosFile::AssertValid() const
{
}

void CDosFile::Dump(CDumpContext& dc) const
{
}
#endif //_DEBUG

void CDosFile::Parse(track rTSB[], int t, int s, int b)
{
	int tKey = rTSB[t][s][b++];

	m_bDeleted = (tKey==0xFF);
	if (m_bDeleted)
		return;

	int sKey = rTSB[t][s][b++];

	if (!tKey && !sKey)
	{
		m_bDeleted = TRUE;
		return;
	}

	m_bLocked = rTSB[t][s][b] & 0x80;
	m_nFileType = rTSB[t][s][b++] & 0x7F;

	m_strName = CString((LPTSTR)&rTSB[t][s][b],0x1E);
	FixDosName();
	b += 0x1E;

	int cSector = *(short*)(&rTSB[t][s][b]);

	m_nLen = 0;
	int tNext, sNext;
	do
	{
		b = 0x0C;
		cSector--; // for t/s-list sector
		while (b<BYTES_PER_SECTOR && cSector)
		{
			int t = rTSB[tKey][sKey][b++];
			int s = rTSB[tKey][sKey][b++];
			m_strContents += ReadFileSector(rTSB,t,s);
			m_nLen += BYTES_PER_SECTOR;
			cSector--;
		}
		tNext = rTSB[tKey][sKey][1];
		sNext = rTSB[tKey][sKey][2];
		tKey = tNext;
		sKey = sNext;
	}
	while (tNext && sNext);

	m_nAuxType = 0;

	if (m_nFileType==0x04) // binary
	{
		unsigned short* ps = (unsigned short*)(LPCTSTR)m_strContents;
		m_nAuxType = *ps++;
		m_nLen = *ps++;
		m_strContents = m_strContents.Mid(4,m_nLen);
	}
}

void CDosFile::FixDosName()
{
	CString s;

	// strip high bit
	for (int i(0); i<m_strName.GetLength(); i++)
		s += m_strName[i] & 0x7F;

	// trim
	s.TrimRight();

	m_strName = s;
}

CString CDosFile::ReadFileSector(track rTSB[], int t, int s)
{
	if (t && s)
		return CString((LPCTSTR)rTSB[t][s],BYTES_PER_SECTOR);
	else
		return CString('\0',BYTES_PER_SECTOR);
}

CString CDosFile::GetFullName()
{
	CString strPath;

	CString str = m_strName;
	CLeftView::FixCase(str);
	strPath = str;

	CDosDir* pDir = m_pParent;
	str = pDir->m_strName;
	CLeftView::FixCase(str);
	strPath = str+"/"+strPath;

	return "/"+strPath;
}

CString CDosFile::GetTypeString()
{
	CString str;
	switch (m_nFileType)
	{
	case 0x00:
		str = "Text";
		break;
	case 0x01:
		str = "Integer BASIC";
		break;
	case 0x02:
		str = "BASIC";
		break;
	case 0x04:
		str = "Binary";
		break;
	case 0x08:
		str = "Special";
		break;
	case 0x10:
		str = "Relocatable";
		break;
	case 0x20:
		str = "BASIC (alt)";
		break;
	case 0x40:
		str = "Binary (alt)";
		break;
	default:
		str.Format("$%2.2X",m_nFileType);
	}
	return str;
}

CString CDosFile::GetDisasm()
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

CString CDosFile::disasm(int nBaseAddress, LPCTSTR rBufferAddr, int nBufferSize)
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

CDosFile::CDosFile(const CDosFile& o):
	m_pParent(o.m_pParent),
	m_bLocked(o.m_bLocked),
	m_bDeleted(o.m_bDeleted),
	m_strName(o.m_strName),
	m_nFileType(o.m_nFileType),
	m_nLen(o.m_nLen),
	m_nAuxType(o.m_nAuxType),
	m_strContents(o.m_strContents)
{
}

CDosFile& CDosFile::operator=(const CDosFile& o)
{
	if (this == &o) return *this;

	m_pParent = o.m_pParent;
	m_bLocked = o.m_bLocked;
	m_bDeleted = o.m_bDeleted;
	m_strName = o.m_strName;
	m_nFileType = o.m_nFileType;
	m_nLen = o.m_nLen;
	m_nAuxType = o.m_nAuxType;
	m_strContents = o.m_strContents;

	return *this;
}

CDosFile::CDosFile(const CProdosFile& o):
	m_pParent(NULL),
	m_bLocked(
		!o.m_nCanDestroy &&
		!o.m_nCanRename &&
		!o.m_nCanWrite),
	m_bDeleted(o.m_bDeleted),
	m_strName(o.m_strName),
	m_nLen(o.m_nLen),
	m_nAuxType(o.m_nAuxType),
	m_strContents(o.m_strContents)
{
	switch (o.m_nFileType)
	{
	case 0x04:
		m_nFileType = 0x00; //Text
		break;
	case 0xFC:
		m_nFileType = 0x02; //BASIC
		break;
	case 0x06:
		m_nFileType = 0x04; //Binary
		break;
	default:
		m_nFileType = 0x04; // default binary
	}
}
