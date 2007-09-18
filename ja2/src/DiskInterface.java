/*
 * Created on Sep 12, 2007
 */
public class DiskInterface
{
	private final DiskDriveSimple disk;
	private final StepperMotor arm;

	private boolean write;

	/**
	 * @param disk
	 * @param motor
	 */
	public DiskInterface(final DiskDriveSimple disk, final StepperMotor arm)
	{
		this.disk = disk;
		this.arm = arm;
	}

	public byte io(final int addr, final byte data)
	{
		final int q = (addr & 0x000E) >> 1;
		final boolean on = (addr & 0x0001) != 0;

		byte ret = -1;
		switch (q)
		{
			case 0:
			case 1:
			case 2:
			case 3:
				this.arm.setMagnet(q,on);
				this.disk.setTrack(this.arm.getTrack());
			break;
			case 4:
				this.disk.setMotorOn(on);
			break;
			case 5:
				this.disk.setDrive2(on);
			break;
			case 6:
				if (on && this.write)
				{
					this.disk.set(data);
				}
				else if (!(on || this.write))
				{
					ret = this.disk.get();
				}
			break;
			case 7:
				this.write = on;
				if (this.disk.isWriteProtected())
				{
					ret |= 0x80;
				}
				else
				{
					ret &= 0x7F;
				}
			break;
		}
		return ret;
	}
}
