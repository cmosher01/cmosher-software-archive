package nu.mosher.mine.a2diskedit;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class A2DiskContents
{
	private final A2DiskImage image;
	public A2DiskContents(A2DiskImage img)
	{
		image = img;
	}

	public void parse(int osType)
	{
		if (osType == A2DiskImage.DOS33)
		{
			parseDir(0x11,0);
		}
		else if (osType == A2DiskImage.PRODOS)
		{
		}
	}

	private void parseDir(int track, int sector)
	{
	}
}
/*
void CDosDir::Parse(track rTSB[], ts tsCat)
{
	m_nDiskVolume = rTSB[tsCat.track][tsCat.sector][6];
	m_strName.Format("Disk volume %d",m_nDiskVolume);

	m_tsCatalog = tsCat;
	NextCat(rTSB);

	while (m_tsCatalog)
	{
		ParseEntries(rTSB);
		NextCat(rTSB);
	}
}

void CDosDir::NextCat(track rTSB[])
{
	m_tsCatalog = ts(
		rTSB[m_tsCatalog.track][m_tsCatalog.sector][1],
		rTSB[m_tsCatalog.track][m_tsCatalog.sector][2]);
}

void CDosDir::ParseEntries(track rTSB[])
{
	int nFileCount(7);
	int b(0xB);
	for (int i(0); i<nFileCount; i++)
	{
		CDosFile* pf = new CDosFile(this);
		pf->Parse(rTSB,m_tsCatalog.track,m_tsCatalog.sector,b);
		if (!pf->m_bDeleted)
			m_rpFile.Add(pf);
		else
			delete pf;
		b += 0x23;
	}
}

CString CDosDir::GetFullName()
{
	return "/"+m_strName;
}

CDosDir::CDosDir(const CDosDir& o):
	m_strName(o.m_strName),
	m_nDiskVolume(o.m_nDiskVolume),
	m_tsCatalog(o.m_tsCatalog)
{
	for (int i(0); i<o.m_rpFile.GetSize(); i++)
	{
		CDosFile* pf = new CDosFile(*o.m_rpFile[i]);
		pf->m_pParent = this;
		m_rpFile.Add(pf);
	}
}

CDosDir& CDosDir::operator=(const CDosDir& o)
{
	if (this == &o) return *this;
	m_strName = o.m_strName;
	m_nDiskVolume = o.m_nDiskVolume;
	m_tsCatalog = o.m_tsCatalog;
	for (int i(0); i<o.m_rpFile.GetSize(); i++)
	{
		CDosFile* pf = new CDosFile(*o.m_rpFile[i]);
		pf->m_pParent = this;
		m_rpFile.Add(pf);
	}

	return *this;
}

CDosDir::CDosDir(const CProdosDir& o):
	m_strName(o.m_strName),
	m_nDiskVolume(254),
	m_tsCatalog(ts(11,0))
{
	for (int i(0); i<o.m_rpFile.GetSize(); i++)
	{
		CDosFile* pf = new CDosFile(*o.m_rpFile[i]);
		pf->m_pParent = this;
		m_rpFile.Add(pf);
	}
}

int CDosDir::CatalogBlocks()
{
	return 0;//???
}

int CDosDir::TotalBlocks(BOOL bRecurse)
{
	return 0;//???
}

int CDosDir::FreeMapBlocks()
{
	return 0;//???
}
*/
