package disk;

/*
 * Created on Sep 16, 2007
 */
public class DiskDriveSimple
{
	private DiskBytes[] disk;
	private int track;
	private int dr; // 0 = drive 1; 1 = drive 2
	private boolean motorOn;

	public DiskDriveSimple(final DiskBytes[] disk)
	{
		this.disk = disk;
	}

	public byte get()
	{
		if (!this.motorOn)
		{
			return -1;
		}
		return this.disk[this.dr].get(this.track);
	}

	public void set(final byte value)
	{
		if (!this.motorOn || this.isWriteProtected())
		{
			return;
		}
		this.disk[this.dr].put(this.track,value);
	}

	public boolean isWriteProtected()
	{
		return this.disk[this.dr].isWriteProtected();
	}

	public void setDrive2(final boolean drive2)
	{
		this.dr = drive2 ? 1 : 0;
	}

	public void setMotorOn(final boolean on)
	{
		this.motorOn = on;
	}

	public void setTrack(final int t)
	{
		this.track = t;
	}

	public int getDriveNumber()
	{
		return this.dr;
	}

	public boolean isMotorOn()
	{
		return this.motorOn;
	}
}
