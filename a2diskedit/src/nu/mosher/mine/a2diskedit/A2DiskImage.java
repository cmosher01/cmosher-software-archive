package nu.mosher.mine.a2diskedit;

public class A2DiskImage
{
	private final byte[] img;

	private final int BYTES_PER_BLOCK = 0x200;

	private final int BYTES_PER_SECTOR = 0x100;
	private final int SECTORS_PER_TRACK = 0x10;

	static final int UNKNOWN = 0;
	static final int DOS33 = 1;
	static final int PRODOS = 2;

	private int osType = UNKNOWN;

	private A2DiskContents contents = null;

	public A2DiskImage(byte[] image)
	{
		img = image;
		parseImage();
	}

	public byte getByte(int track, int sector, int offset)
	{
		int i = (track*SECTORS_PER_TRACK+sector)*BYTES_PER_SECTOR + offset;
		return img[i];
	}

	public byte getByte(int block, int offset)
	{
		int i = block*BYTES_PER_BLOCK + offset;
		return img[i];
	}

	public byte[] getBytes(int track, int sector, int offset, int c)
	{
		byte[] rb = new byte[c];
		System.arraycopy(img,(track*SECTORS_PER_TRACK+sector)*BYTES_PER_SECTOR+offset,rb,0,c);
		return rb;
	}

	private void parseImage()
	{
		fixOrder();
		contents = new A2DiskContents(this);
		contents.parse(osType);
	}

	private void fixOrder()
	{
		if (
			getByte(0,0xB,0)==0 &&
			getByte(0,0xB,1)==0 &&
			getByte(0,0xB,2)==3 &&
			getByte(0,0xB,3)==0 &&
			getByte(0,0xB,4)>>4==0xF)
		{
			osType = PRODOS;
			// Prodos disk in track-sector order
			// switch to block order
			switchTSBlock();
		}
		else if (
			getByte(2,0)==0 &&
			getByte(2,1)==0 &&
			getByte(2,2)==3 &&
			getByte(2,3)==0 &&
			getByte(2,4)>>4==0xF)
		{
			osType = PRODOS;
			// Prodos disk in block order
			// OK
		}
		else if (
			getByte(0x11,0xE,1)==0x11 &&
			getByte(0x11,0xE,2)==0x0D)
		{
			osType = DOS33;
			// DOS 3.3 disk in track-sector order
			// OK
		}
		else if (
			getByte(0x88,0x101)==0x11 &&
			getByte(0x88,0x102)==0x0D)
		{
			osType = DOS33;
			// DOS 3.3 disk in block order
			// switch to track-sector order
			switchTSBlock();
		}
		else
		{
			osType = UNKNOWN;
		}
	}

	private void switchTSBlock()
	{
	}

	public A2DiskContents getContents()
	{
		return contents;
	}
}
