package disk;

import java.io.File;
import java.io.IOException;

/*
 * Created on Sep 16, 2007
 */
class DiskDriveSimple
{
	private final DiskBytes disk;
	private final StepperMotor arm;



	public DiskDriveSimple(final DiskBytes disk, final StepperMotor arm)
	{
		this.disk = disk;
		this.arm = arm;
	}



	public void loadDisk(final File fnib) throws IOException, InvalidDiskImage
	{
		this.disk.load(fnib);
	}

	public boolean isWriteProtected()
	{
		return this.disk.isWriteProtected();
	}

	public boolean isModified()
	{
		return this.disk.isModified();
	}



	public void setMagnet(int q, boolean on)
	{
		this.arm.setMagnet(q,on);
	}

	public int getTrack()
	{
		return this.arm.getTrack();
	}



	public byte get()
	{
		return this.disk.get(this.arm.getTrack());
	}

	void set(final byte value)
	{
		this.disk.put(this.arm.getTrack(),value);
	}



	DiskBytes getDiskBytes()
	{
		return this.disk;
	}
}
