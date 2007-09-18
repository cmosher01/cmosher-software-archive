import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * Created on Sep 16, 2007
 */
public class DiskBytes
{
	private static final int EOF = -1;
	private static final int BYTES_PER_TRACK = 0x1A00;
	private static final int TRACKS_PER_DISK = 0x23;

	private byte[][] bytes = new byte[TRACKS_PER_DISK][BYTES_PER_TRACK];

	private boolean writable;
	private boolean loaded;
	private int byt;

	public DiskBytes()
	{
		unload();
	}

	public void load(final File f) throws IOException
	{
		final InputStream disk = new BufferedInputStream(new FileInputStream(f));
		System.out.println("Loading "+f.getCanonicalPath());
		int itrack = 0;
		for (int b1 = disk.read(); b1 != EOF && itrack < TRACKS_PER_DISK; b1 = disk.read())
		{
			this.bytes[itrack][this.byt] = (byte)b1;
			if (nextByte())
			{
				++itrack;
			}
		}
		disk.close();
		this.writable = f.canWrite();
		this.loaded = true;
	}

	public void unload()
	{
		this.byt = 0;
		this.writable = true;
		this.loaded = false;
	}

	public byte get(final int track)
	{
		if (track < 0 || TRACKS_PER_DISK <= track)
		{
			throw new IllegalStateException();
		}
		if (!this.loaded)
		{
			return 0;
		}
		final byte ret = this.bytes[track][this.byt];
		nextByte();
		//System.out.println("DISK: getting bit "+Integer.toHexString(this.bit)+": "+ret);
		return ret;
	}

	public void put(final int track, final byte value)
	{
		if (track < 0 || TRACKS_PER_DISK <= track)
		{
			throw new IllegalStateException();
		}
		if (!this.writable || !this.loaded)
		{
			return;
		}
//		System.out.println("DISK: writing byte "+Integer.toHexString((value&0xFF))+" @"+Integer.toHexString(this.byt));
		this.bytes[track][this.byt] = value;
		nextByte();
	}

	public boolean nextByte()
	{
		++this.byt;
		final boolean nextTrack = this.byt >= BYTES_PER_TRACK;
		if (nextTrack)
		{
//			System.out.println("Disk completed one rotation ----------------");
			this.byt = 0;
		}
		return nextTrack;
	}

	public boolean isWriteProtected()
	{
		return !this.writable;
	}
}
