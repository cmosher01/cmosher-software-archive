/*
 * Created on Nov 30, 2007
 */
package disk;

public class DiskState
{
	/* accessed only by DiskController */
	final DiskDriveSimple disk;
	final StepperMotor arm;
	boolean write;

	public DiskState(final DiskDriveSimple disk, final StepperMotor arm)
	{
		this.disk = disk;
		this.arm = arm;
	}

	public int getCurrentDriveNumber()
	{
		return this.disk.getDriveNumber();
	}

	public int getOtherDriveNumber()
	{
		return 1-this.disk.getDriveNumber();
	}

	public int getTrack()
	{
		return this.arm.getTrack();
	}

	public boolean isWriteProtected()
	{
		return this.disk.isWriteProtected();
	}

	public boolean isModified()
	{
		return this.disk.isModified();
	}

	public boolean isModifiedOther()
	{
		return this.disk.isModifiedOther();
	}

	public boolean isMotorOn()
	{
		return this.disk.isMotorOn();
	}

	public boolean isWriting()
	{
		return this.write;
	}
}
