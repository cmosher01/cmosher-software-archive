package nu.mosher.mine.a2diskedit;

import java.util.ArrayList;
import java.util.List;

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
	private int dos33volume = 0;
	private String sVolumeName = "";
	private List rCatEntry = new ArrayList(30);

	private int catTrack;
	private int catSector;

	private static final int ENTRY_SIZE = 0x23;

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
		byte svol = image.getByte(track,sector,6);
		if (svol<0)
			dos33volume = 256+svol;
		else
			dos33volume = svol;
		sVolumeName = "Disk volume "+Integer.toString(dos33volume);

		catTrack = track;
		catSector = sector;
		calcNextCat();

		while (catTrack > 0 && catSector > 0)
		{
			parseCatalog();
			calcNextCat();
		}
	}

	private void calcNextCat()
	{
		int t = image.getByte(catTrack,catSector,1);
		int s = image.getByte(catTrack,catSector,2);
		catTrack = t;
		catSector = s;
	}
	private void parseCatalog()
	{
		int cFile = 7;
		int b = 0xB;
		for (int i = 0; i < cFile; ++i)
		{
			byte[] catBytes = image.getBytes(catTrack,catSector,b,ENTRY_SIZE);
			CatEntryDos33 ce = new CatEntryDos33(catBytes);
			if (ce.isDisplayable())
				rCatEntry.add(ce);
			b += ENTRY_SIZE;
		}
	}

	public String getVolumeName()
	{
		return sVolumeName;
	}

	public static String dosName(byte[] r)
	{
		StringBuffer s = new StringBuffer(r.length);

		// strip high bit
		for (int i = 0; i < r.length; ++i)
			s.append((char)(r[i] & 0x7F));

		// trim
		return s.toString().trim();
	}

	public void getCatList(List r)
	{
		r.addAll(rCatEntry);
	}
}
