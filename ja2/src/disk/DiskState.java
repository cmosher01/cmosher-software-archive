/*
 * Created on Nov 30, 2007
 */
package disk;

import java.io.File;
import java.io.IOException;

class DiskState
{
	final DiskDriveSimple disk1;
	final DiskDriveSimple disk2;

	int currentDrive; // 0 = drive 1; 1 = drive 2
	boolean motorOn;
	boolean write;



	public DiskState(final DiskDriveSimple disk1, final DiskDriveSimple disk2)
	{
		this.disk1 = disk1;
		this.disk2 = disk2;
	}

	public boolean isMotorOn()
	{
		return this.motorOn;
	}

	public int getCurrentDriveNumber()
	{
		return this.currentDrive;
	}

	public int getOtherDriveNumber()
	{
		return 1-this.currentDrive;
	}



	private DiskDriveSimple getCurrentDrive()
	{
		return (this.currentDrive == 0) ? this.disk1 : this.disk2;
	}

	private DiskDriveSimple getOtherDrive()
	{
		return (this.currentDrive == 1) ? this.disk1 : this.disk2;
	}

	private DiskDriveSimple getDrive(final int drive)
	{
		return (drive == 0) ? this.disk1 : this.disk2;
	}

	public int getTrack()
	{
		return this.getCurrentDrive().getTrack();
	}

	public boolean isWriteProtected()
	{
		return this.getCurrentDrive().isWriteProtected();
	}

	public boolean isModified()
	{
		return this.getCurrentDrive().isModified();
	}

	public boolean isModifiedOther()
	{
		return this.getOtherDrive().isModified();
	}

	public void loadDisk(final int drive, final File fnib) throws IOException, InvalidDiskImage
	{
		this.getDrive(drive).loadDisk(fnib);
	}

	public void setMagnet(final int q, final boolean on)
	{
		this.getCurrentDrive().setMagnet(q,on);
	}

	public void setMotorOn(final boolean on)
	{
		this.motorOn = on;
	}

	public void setDrive2(final boolean on)
	{
		this.currentDrive = (on ? 1 : 0);
	}

	public void set(final byte data)
	{
		if (!this.motorOn || this.isWriteProtected())
		{
			return;
		}
		this.getCurrentDrive().set(data);
	}

	public byte get()
	{
		if (!this.motorOn)
		{
			return -1;
		}
		return this.getCurrentDrive().get();
	}

	public DiskBytes getDiskBytes(int disk)
	{
		return getDrive(disk).getDiskBytes();
	}
}
