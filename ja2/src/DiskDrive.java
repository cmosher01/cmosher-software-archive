import java.io.File;
import java.io.IOException;

/*
 * Created on Sep 12, 2007
 */
public class DiskDrive implements Clock.Timed
{
	private static final int DRIVES = 2;
	private Disk2[] disk = new Disk2[DRIVES];
	private int t;
	private int track;
	private int dr; // 0 = drive 1; 1 = drive 2
	private byte latch;
	private boolean writing;
	private boolean motorOn;

	public DiskDrive()
	{
		for (int i = 0; i < DRIVES; ++i)
		{
			this.disk[i] = new Disk2();
		}
	}
	public void stopped()
	{
		
	}
	public void tick()
	{
		if (!this.motorOn)
		{
			this.latch = -1;
			return;
		}

		++this.t;
		if (this.t >= 4)
		{
			boolean outofsynch = false;
			if (this.writing)
			{
				final int bittowrite = (this.latch & 0x80) >> 7;
				this.disk[this.dr].put(this.track,bittowrite);
				System.out.println("DISK: writing "+bittowrite+" @"+Integer.toHexString(this.disk[this.dr].bit));
				this.latch <<= 1;
			}
			else
			{
				if (this.latch < 0)
				{
					outofsynch = true;
//					System.out.println("DISK: waiting for CPU...");
				}
				else
				{
					this.latch <<= 1;
					this.latch |= this.disk[this.dr].get(this.track);
//					System.out.println("DISK: READ: latch set to ---> "+Integer.toHexString(((int)latch)&0xFF)+"    "+Integer.toBinaryString(((int)latch)&0xFF));
				}
			}

			if (!outofsynch)
			{
				this.disk[this.dr].rotate();
			}

			this.t = 0;
		}
	}

	public byte get()
	{
		return get(true);
	}

	public byte get(boolean clearIfValid)
	{
		final byte ret = this.latch;
		if (ret < 0 && clearIfValid)
		{
			//System.out.println("DISK: READ: reading negative latch: "+Integer.toHexString(((int)ret)&0xFF));
			this.latch = 0;
			if (this.writing)
				System.out.println("CLEARING DISK LATCH WHILE WRITING");
		}
		return ret;
	}

	public void set(final byte value)
	{
		this.latch = value;
		System.out.println("SETTING DISK LATCH TO "+Integer.toHexString(value));
	}

	public void setReadWriteMode(final boolean writing)
	{
		if (writing)
			System.out.println("DISK: write-mode");
		else
			System.out.println("DISK: read-mode");
		this.writing = writing;
	}

	public boolean isWriteProtected()
	{
		return this.disk[this.dr].isWriteProtected();
	}

	public void setDrive(final int drive)
	{
		if (drive < 0 || DRIVES <= drive)
		{
			throw new IllegalStateException();
		}
		System.out.println("DISK: drive "+drive);
		this.dr = drive;
	}

	public void setMotorOn(final boolean on)
	{
		if (on)
			System.out.println("DISK: drive on");
		else
			System.out.println("DISK: drive off");
		this.motorOn = on;
	}

	public boolean isMotorOn()
	{
		return this.motorOn;
	}

	public void load(final int drive, final File diskette) throws IOException
	{
		this.disk[drive].load(diskette);
	}

	public void unload(final int drive)
	{
		this.disk[drive].unload();
	}

	public void setTrack(final int t)
	{
//		System.out.println("DISK: track "+t);
		this.track = t;
	}
}
