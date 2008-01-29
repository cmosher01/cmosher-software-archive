package cards.disk;

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
import keyboard.HyperMode;
import util.Util;

/*
 * Created on Sep 16, 2007
 */
public class DiskBytes
{
	private static final int BYTES_PER_TRACK = 0x1A00;

	private final HyperMode hyper;

	private byte[][] bytes;

	private volatile File file;
	private volatile String fileName;
	private final AtomicBoolean writable = new AtomicBoolean();
	private final AtomicBoolean loaded = new AtomicBoolean();
	private int byt; // represents rotational position of disk
	private int waitTimes;
	private final AtomicBoolean modified = new AtomicBoolean();

	public DiskBytes(final HyperMode throttle)
	{
		this.hyper = throttle;
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
		if (f.length() != Drive.TRACKS_PER_DISK*BYTES_PER_TRACK)
		{
			throw new InvalidDiskImage(f.getName());
		}
		byte[][] rb = new byte[Drive.TRACKS_PER_DISK][BYTES_PER_TRACK];
		final InputStream disk = new BufferedInputStream(new FileInputStream(f));
		int itrack = 0;
		for (int b1 = disk.read(); b1 != Util.EOF && itrack < Drive.TRACKS_PER_DISK; b1 = disk.read())
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
		this.writable.set(f.canWrite());
		synchronized (this.loaded)
		{
			this.loaded.set(true);
			this.loaded.notifyAll();
		}
		this.modified.set(false);
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
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(this.file),Drive.TRACKS_PER_DISK*BYTES_PER_TRACK);
		for (int itrack = 0; itrack < Drive.TRACKS_PER_DISK; ++itrack)
		{
			for (int ibyte = 0; ibyte < BYTES_PER_TRACK; ++ibyte)
			{
				out.write(this.bytes[itrack][ibyte]);
			}
		}
		out.flush();
		out.close();
		this.modified.set(false);
	}

	public void unload()
	{
		this.byt = 0;
		this.writable.set(true);
		synchronized (this.loaded)
		{
			this.loaded.set(false);
			this.loaded.notifyAll();
		}
		this.file = null;
		this.fileName = "";
		this.modified.set(false);
	}

	public byte get(final int track)
	{
		if (track < 0 || Drive.TRACKS_PER_DISK <= track)
		{
			throw new IllegalStateException();
		}
		if (!isLoaded())
		{
			waitIfTooFast();
			if (!isLoaded())
			{
				return -1;
			}
		}
		final byte ret = this.bytes[track][this.byt];
		nextByte();
		return ret;
	}

	private void waitIfTooFast()
	{
		if (this.hyper.isHyper())
		{
			return;
		}

		++this.waitTimes;
		if (this.waitTimes >= 0x100)
		{
			synchronized (this.loaded)
			{
				try
				{
					this.loaded.wait(50);
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
			this.waitTimes = 0;
		}
	}

	void put(final int track, final byte value)
	{
		if (track < 0 || Drive.TRACKS_PER_DISK <= track)
		{
			throw new IllegalStateException();
		}
		if (isWriteProtected() || !isLoaded())
		{
			return;
		}
		this.bytes[track][this.byt] = value;
		this.modified.set(true);
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
		return !this.writable.get();
	}

	/**
	 * @return the modified
	 */
	public boolean isModified()
	{
		return this.modified.get();
	}
}
