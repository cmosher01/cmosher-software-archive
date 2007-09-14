import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

/*
 * Created on Sep 12, 2007
 */
public class Disk2
{
	private static final int EOF = -1;
	private static final int BITS_PER_TRACK = 0xD000;
	private static final int TRACKS_PER_DISK = 0x23;

	private BitSet[] bits = new BitSet[TRACKS_PER_DISK];
	private boolean writable;
	private boolean loaded;
	private volatile int bit;

	public Disk2()
	{
		unload();
	}

	public void load(final File f) throws IOException
	{
		final InputStream disk = new BufferedInputStream(new FileInputStream(f));
		System.out.println("Loading "+f.getCanonicalPath());
		int bit = 0;
		int track = 0;
		for (int byt = disk.read(); byt != EOF && track < TRACKS_PER_DISK; byt = disk.read())
		{
			for (int i = 0; i < 8; ++i)
			{
				this.bits[track].set(bit++,(byt & 0x80) != 0);
				byt <<= 1;
			}
			if (bit >= BITS_PER_TRACK)
			{
				++track;
				bit = 0;
			}
		}
		disk.close();
		System.out.println("Loaded up to track "+track+", bit "+bit);
//		showBits(this.bits[0].get(0,0x200));
		this.writable = f.canWrite();
		this.loaded = true;
	}

	private void showBits(BitSet set)
	{
		for (int i = 0; i < set.length(); ++i)
		{
			if (set.get(i))
				System.out.print("1");
			else
				System.out.print("0");
			if (i % 0x80 == 0x7f)
				System.out.println();
		}
	}

	public void unload()
	{
		for (int i = 0; i < this.bits.length; ++i)
		{
			bits[i] = new BitSet(BITS_PER_TRACK);
		}
		this.bit = 0;
		this.writable = true;
		this.loaded = false;
	}

	public int get(final int track)
	{
		if (track < 0 || TRACKS_PER_DISK <= track)
		{
			throw new IllegalStateException();
		}
		final int ret = this.bits[track].get(this.bit) ? 1 : 0;
		//System.out.println("DISK: getting bit "+Integer.toHexString(this.bit)+": "+ret);
		return ret;
	}

	public void put(final int track, final int value)
	{
		if (track < 0 || TRACKS_PER_DISK <= track || value < 0 || 2 <= value)
		{
			throw new IllegalStateException();
		}
		if (!this.writable || !this.loaded)
		{
			return;
		}
		this.bits[track].set(this.bit,value != 0);
	}

	public void rotate()
	{
		++this.bit;
		if (this.bit >= BITS_PER_TRACK)
		{
			this.bit = 0;
		}
	}

	public boolean isWriteProtected()
	{
		return !this.writable;
	}
}
