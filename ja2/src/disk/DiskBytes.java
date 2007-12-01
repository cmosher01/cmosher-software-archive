package disk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	private volatile File file;
	private volatile String fileName;
	private final AtomicBoolean writable = new AtomicBoolean();
	private final AtomicBoolean loaded = new AtomicBoolean();
	private int byt;
	private int waitTimes;

	public DiskBytes()
	{
		unload();
	}

	public void load(File f) throws IOException, InvalidDiskImage
	{
		if (isLoaded())
		{
			unload();
		}
		f = f.getCanonicalFile();
		if (!f.exists())
		{
			throw new FileNotFoundException("Can't find file: "+f.getPath());
		}
		if (f.length() != TRACKS_PER_DISK*BYTES_PER_TRACK)
		{
			throw new InvalidDiskImage(f.getName());
		}
		byte[][] rb = new byte[TRACKS_PER_DISK][BYTES_PER_TRACK];
		final InputStream disk = new BufferedInputStream(new FileInputStream(f));
		int itrack = 0;
		for (int b1 = disk.read(); b1 != EOF && itrack < TRACKS_PER_DISK; b1 = disk.read())
		{
			if (b1 < 0x96)
			{
				throw new InvalidDiskImage(f.getName());
			}
			rb[itrack][this.byt] = (byte)b1;
			if (nextByte())
			{
				++itrack;
			}
		}
		disk.close();

		this.file = f;
		this.fileName = this.file.getName();
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

	public String getFileName()
	{
		return this.fileName;
	}

	public boolean isLoaded()
	{
		synchronized (this.loaded)
		{
			return this.loaded.get();
		}
	}
	public void save() throws IOException
	{
		if (isWriteProtected() || !isLoaded())
		{
			return;
		}
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(this.file),TRACKS_PER_DISK*BYTES_PER_TRACK);
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
		this.file = null;
		this.fileName = "";
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
						this.loaded.wait(20);
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

	void put(final int track, final byte value)
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

	private boolean nextByte()
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
