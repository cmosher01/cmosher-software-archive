package disk;

import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;
import chipset.Card;
import gui.DiskDriveControllerPanel;
import gui.GUI;

/*
 * Created on Sep 12, 2007
 */
public class DiskController extends Card
{
	private final DiskDriveSimple drive1;
	private final DiskDriveSimple drive2;

	private volatile DiskDriveSimple currentDrive;

	private volatile boolean write;
	private volatile boolean motorOn;


	private DiskDriveControllerPanel panel;


	public DiskController()
	{
		this.drive1 = new DiskDriveSimple(new DiskBytes(),new StepperMotor());
		this.drive2 = new DiskDriveSimple(new DiskBytes(),new StepperMotor());
	}

	@Override
	public byte io(final int addr, final byte data, final boolean writing)
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
				this.currentDrive.setMagnet(q,on);
				this.panel.updateDrives();
			break;
			case 4:
				this.motorOn = on;
				this.panel.updateDrives();
			break;
			case 5:
				this.currentDrive = (on ? this.drive2 : this.drive1);

				this.panel.updateDrives();
			break;
			case 6:
				if (on && this.write)
				{
					set(data);
					this.panel.updateDrives();
				}
				else if (!(on || this.write))
				{
					ret = get();
				}
			break;
			case 7:
				this.write = on;
				this.panel.updateDrives();
				if (this.currentDrive.isWriteProtected())
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
		this.panel.updateDrives();
	}

	private DiskDriveSimple getDrive(final int drive)
	{
		return (drive == 0) ? this.drive1 : this.drive2;
	}

	public void loadDisk(int drive, File fnib) throws IOException, InvalidDiskImage
	{
		this.getDrive(drive).loadDisk(fnib);
	}

	public boolean isMotorOn()
	{
		return this.motorOn;
	}

	/**
	 * @param gui
	 * @return
	 */
	@Override
	public JPanel getPanel(GUI gui)
	{
		if (this.panel == null)
		{
			this.panel = new DiskDriveControllerPanel(this,gui);
		}
		return this.panel;
	}

	/**
	 * @return
	 */
	@Override
	public DropTargetListener getDropListener()
	{
		return this.panel.getDropListener();
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

	private DiskDriveSimple getOtherDrive()
	{
		return (this.currentDrive == this.drive1) ? this.drive2 : this.drive1;
	}
}
