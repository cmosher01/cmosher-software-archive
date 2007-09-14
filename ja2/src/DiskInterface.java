/*
 * Created on Sep 12, 2007
 */
public class DiskInterface
{
	private final DiskDrive disk;
	private final StepperMotor arm;

	private boolean load; // Q6
	private boolean write; // Q7

	/**
	 * @param disk
	 * @param motor
	 */
	public DiskInterface(final DiskDrive disk, final StepperMotor arm)
	{
		this.disk = disk;
		this.arm = arm;
	}

	public int io(final int addr, final byte data)
	{
		final int q = (addr & 0x000F) >> 1;
		final boolean on = (addr & 1) != 0;

		int ret = this.disk.get();
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
				this.disk.setDrive(on ? 1 : 0);
			break;
			case 6:
				this.load = on;
				if (this.load && this.write && this.disk.isMotorOn() && !this.disk.isWriteProtected())
				{
					this.disk.set(data);
				}
			break;
			case 7:
				this.write = on;
				this.disk.setReadWriteMode(on);
				if (this.load && this.disk.isMotorOn())
				{
					if (this.disk.isWriteProtected())
					{
						ret |= 0x80;
					}
					else
					{
						ret &= 0x7F;
					}
				}
			break;
		}
		return ret & 0xFF;
	}
}
