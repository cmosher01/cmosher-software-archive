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

	void set(final byte value)
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

	public boolean isModified()
	{
		return this.disk[this.dr].isModified();
	}

	public boolean isModifiedOther()
	{
		return this.disk[1-this.dr].isModified();
	}

	void setDrive2(final boolean drive2)
	{
		this.dr = drive2 ? 1 : 0;
	}

	void setMotorOn(final boolean on)
	{
		this.motorOn = on;
	}

	void setTrack(final int t)
	{
		this.track = t;
	}

	int getDriveNumber()
	{
		return this.dr;
	}

	public boolean isMotorOn()
	{
		return this.motorOn;
	}
}
