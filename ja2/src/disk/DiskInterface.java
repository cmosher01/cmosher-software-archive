package disk;

import chipset.Card;
import gui.GUI;

/*
 * Created on Sep 12, 2007
 */
public class DiskInterface implements Card
{
	private final DiskDriveSimple disk;
	private final StepperMotor arm;
	private final GUI gui;

	private boolean write;

	/**
	 * @param disk
	 * @param manager 
	 * @param motor
	 */
	public DiskInterface(final DiskDriveSimple disk, final StepperMotor arm, final GUI gui)
	{
		this.disk = disk;
		this.arm = arm;
		this.gui = gui;
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
				updatePanel();
			break;
		}
		return ret;
	}

	public void reset()
	{
		this.disk.setMotorOn(false);
		this.disk.setDrive2(false);
		updatePanel();
	}

	private void updatePanel()
	{
		this.gui.updateDrives();
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

	public boolean isMotorOn()
	{
		return this.disk.isMotorOn();
	}

	public boolean isWriting()
	{
		return this.write;
	}
}
