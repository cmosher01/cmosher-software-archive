package disk;
import gui.FrameManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Created on Sep 16, 2007
 */
public class DiskBytes
{
	private static final int EOF = -1;
	private static final int BYTES_PER_TRACK = 0x1A00;
	private static final int TRACKS_PER_DISK = 0x23;

	private byte[][] bytes;

	private final AtomicBoolean loaded = new AtomicBoolean();
	private final AtomicBoolean writable = new AtomicBoolean();
	private int byt;
	private int waitTimes;
	private final FrameManager framer;

	public DiskBytes(final FrameManager framer)
	{
		this.framer = framer;
		unload();
	}

	public void load(final File f) throws IOException
	{
		boolean bad;
		if (f.length() != TRACKS_PER_DISK*BYTES_PER_TRACK)
		{
			showBadDisk();
			return;
		}
		byte[][] rb = new byte[TRACKS_PER_DISK][BYTES_PER_TRACK];
		final InputStream disk = new BufferedInputStream(new FileInputStream(f));
		int itrack = 0;
		for (int b1 = disk.read(); b1 != EOF && itrack < TRACKS_PER_DISK; b1 = disk.read())
		{
			if (b1 < 0x96)
			{
				showBadDisk();
				return;
			}
			rb[itrack][this.byt] = (byte)b1;
			if (nextByte())
			{
				++itrack;
			}
		}
		disk.close();
		this.bytes = rb;
		synchronized (this.writable)
		{
			this.writable.set(f.canWrite());
		}
		synchronized (this.loaded)
		{
			this.loaded.set(true);
			this.loaded.notifyAll();
		}
	}

	private void showBadDisk()
	{
		this.framer.showMessage("That does not appear to be a valid nibble disk image. This emulator only accepts NIBBLE images.");
	}

	private boolean isLoaded()
	{
		synchronized (this.loaded)
		{
			return this.loaded.get();
		}
	}
	public void save(final File f) throws IOException
	{
		if (isWriteProtected() || !isLoaded())
		{
			return;
		}
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(f),TRACKS_PER_DISK*BYTES_PER_TRACK);
		for (int itrack = 0; itrack < TRACKS_PER_DISK; ++itrack)
		{
			for (int ibyte = 0; ibyte < BYTES_PER_TRACK; ++ibyte)
			{
				out.write(this.bytes[itrack][ibyte]);
			}
		}
		out.flush();
		out.close();
	}

	public void unload()
	{
		this.byt = 0;
		synchronized (this.writable)
		{
			this.writable.set(true);
		}
		synchronized (this.loaded)
		{
			this.loaded.set(false);
			this.loaded.notifyAll();
		}
	}

	public byte get(final int track)
	{
		if (track < 0 || TRACKS_PER_DISK <= track)
		{
			throw new IllegalStateException();
		}
		synchronized (this.loaded)
		{
			if (!this.loaded.get())
			{
				++this.waitTimes;
				if (this.waitTimes >= 0x100)
				{
					this.waitTimes = 0;
					try
					{
						this.loaded.wait(100);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				}
			}
			if (!this.loaded.get())
			{
				return -1;
			}
		}
		final byte ret = this.bytes[track][this.byt];
		nextByte();
		return ret;
	}

	public void put(final int track, final byte value)
	{
		if (track < 0 || TRACKS_PER_DISK <= track)
		{
			throw new IllegalStateException();
		}
		if (isWriteProtected() || !isLoaded())
		{
			return;
		}
		this.bytes[track][this.byt] = value;
		nextByte();
	}

	public boolean nextByte()
	{
		++this.byt;
		final boolean nextTrack = this.byt >= BYTES_PER_TRACK;
		if (nextTrack)
		{
			this.byt = 0;
		}
		return nextTrack;
	}

	public boolean isWriteProtected()
	{
		synchronized (this.writable)
		{
			return !this.writable.get();
		}
	}
}
