package nu.mosher.mine.a2diskedit;

public class A2DiskImage
{
	private final byte[] img;

	private final int BYTES_PER_BLOCK = 0x200;

	private final int BYTES_PER_SECTOR = 0x100;
	private final int SECTORS_PER_TRACK = 0x10;

	public A2DiskImage(byte[] image)
	{
		img = image;
	}

	public byte getByte(int track, int sector, int offset)
	{
		int i = (track*SECTORS_PER_TRACK+sector)*BYTES_PER_SECTOR + offset;
		return byte[i];
	}

	public byte getByte(int block, int offset)
	{
		int i = block*BYTES_PER_BLOCK + offset;
		return byte[i];
	}
}
