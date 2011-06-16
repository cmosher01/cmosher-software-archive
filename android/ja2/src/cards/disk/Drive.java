package cards.disk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/*
 * Created on Sep 16, 2007
 */
class Drive
{
	public static final int TRACKS_PER_DISK = 0x23;

	private final DiskBytes disk;
	private final StepperMotor arm;



	public Drive(final DiskBytes disk, final StepperMotor arm)
	{
		this.disk = disk;
		this.arm = arm;
	}



	public void loadDisk(final InputStream nib, final String filename) throws IOException, InvalidDiskImage
	{
		this.disk.load(nib,filename);
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

	public void set(final byte value)
	{
		this.disk.put(this.arm.getTrack(),value);
	}



	public DiskBytes getDiskBytes()
	{
		return this.disk;
	}
}
