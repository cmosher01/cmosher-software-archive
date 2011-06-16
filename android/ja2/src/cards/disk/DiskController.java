package cards.disk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import keyboard.HyperMode;
import cards.Card;

/*
 * Created on Sep 12, 2007
 */
public class DiskController extends Card
{
	private final Drive drive1;
	private final Drive drive2;

	private volatile Drive currentDrive;

	private volatile boolean write;
	private volatile boolean motorOn;



	// TODO for a rev. 0 motherboard, the disk controller will auto reset the CPU

	public DiskController(final HyperMode hyper)
	{
		this.drive1 = new Drive(new DiskBytes(hyper),new StepperMotor());
		this.drive2 = new Drive(new DiskBytes(hyper),new StepperMotor());
		this.currentDrive = this.drive1;
	}

	@Override
	public byte io(final int addr, byte data, @SuppressWarnings("unused") final boolean writing)
	{
		final int q = (addr & 0x000E) >> 1;
		final boolean on = (addr & 0x0001) != 0;

		switch (q)
		{
			case 0:
			case 1:
			case 2:
			case 3:
				this.currentDrive.setMagnet(q,on);
				update();
			break;
			case 4:
				this.motorOn = on;
				update();
			break;
			case 5:
				this.currentDrive = (on ? this.drive2 : this.drive1);

				update();
			break;
			case 6:
				if (on && this.write)
				{
					set(data);
					update();
				}
				else if (!(on || this.write))
				{
					data = get();
				}
			break;
			case 7:
				this.write = on;
				update();
				if (this.currentDrive.isWriteProtected())
				{
					data |= 0x80;
				}
				else
				{
					data &= 0x7F;
				}
			break;
		}
		return data;
	}

	private void set(final byte data)
	{
		if (!this.motorOn)
		{
			return;
		}
		this.currentDrive.set(data);
	}

	private byte get()
	{
		if (!this.motorOn)
		{
			return -1;
		}
		return this.currentDrive.get();
	}

	@Override
	public void reset()
	{
		this.motorOn = false;
		this.currentDrive = this.drive1;
		update();
	}

	private void update()
	{
//		if (this.panel == null)
//		{
//			return;
//		}
//		this.panel.updateDrives();
	}
	private Drive getDrive(final int drive)
	{
		return (drive == 0) ? this.drive1 : this.drive2;
	}

	public void loadDisk(int drive, InputStream nib, String filename) throws IOException, InvalidDiskImage
	{
		this.getDrive(drive).loadDisk(nib,filename);
	}

	public boolean isMotorOn()
	{
		return this.motorOn;
	}

	public DiskBytes getDiskBytes(int disk)
	{
		return this.getDrive(disk).getDiskBytes();
	}

	public int getTrack()
	{
		return this.currentDrive.getTrack();
	}

	public boolean isWriting()
	{
		return this.write;
	}

	public boolean isModified()
	{
		return this.currentDrive.isModified();
	}

	public boolean isModifiedOther()
	{
		return getOtherDrive().isModified();
	}

	public boolean isWriteProtected()
	{
		return this.currentDrive.isWriteProtected();
	}

	public boolean isDirty()
	{
		return isModified() || isModifiedOther();
	}

	public int getCurrentDriveNumber()
	{
		return this.currentDrive == this.drive1 ? 0 : 1;
	}

	public int getOtherDriveNumber()
	{
		return 1-getCurrentDriveNumber();
	}

	private Drive getOtherDrive()
	{
		return (this.currentDrive == this.drive1) ? this.drive2 : this.drive1;
	}
}
