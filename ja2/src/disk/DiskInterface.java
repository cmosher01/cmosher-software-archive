package disk;
import buttons.DiskDrivePanel;
import gui.FrameManager;

/*
 * Created on Sep 12, 2007
 */
public class DiskInterface
{
	private final DiskDriveSimple disk;
	private final StepperMotor arm;
	private final FrameManager framer;

	private boolean write;

	/**
	 * @param disk
	 * @param manager 
	 * @param motor
	 */
	public DiskInterface(final DiskDriveSimple disk, final StepperMotor arm, FrameManager framer)
	{
		this.disk = disk;
		this.arm = arm;
		this.framer = framer;
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
				updatePanel();
			break;
			case 4:
				this.disk.setMotorOn(on);
				updatePanel();
			break;
			case 5:
				this.disk.setDrive2(on);
				updatePanel();
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

	public void updatePanel()
	{
		DiskDrivePanel drive = this.framer.getDrive(this.disk.getDriveNumber());
		if (drive == null)
		{
			return;
		}
		drive.setTrack(this.arm.getTrack());
		if (this.disk.isMotorOn())
		{
			if (this.write)
			{
				this.framer.getDrive(this.disk.getDriveNumber()).setWriting(true);
				this.framer.getDrive(this.disk.getDriveNumber()).setReading(false);
			}
			else
			{
				this.framer.getDrive(this.disk.getDriveNumber()).setWriting(false);
				this.framer.getDrive(this.disk.getDriveNumber()).setReading(true);
			}
		}
		else
		{
			this.framer.getDrive(this.disk.getDriveNumber()).setWriting(false);
			this.framer.getDrive(this.disk.getDriveNumber()).setReading(false);
		}
		this.framer.getDrive(1-this.disk.getDriveNumber()).setWriting(false);
		this.framer.getDrive(1-this.disk.getDriveNumber()).setReading(false);
	}
}
