package disk;

import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;
import chipset.Card;
import gui.DiskDriveControllerPanel;
import gui.GUI;
import gui.UI;

/*
 * Created on Sep 12, 2007
 */
public class DiskController extends Card
{
	private final DiskBytes disk1 = new DiskBytes();
	private final DiskBytes disk2 = new DiskBytes();
	private final DiskDriveSimple drive = new DiskDriveSimple(new DiskBytes[] {disk1,disk2});
	private final StepperMotor arm = new StepperMotor();
	private final DiskState state = new DiskState(drive,arm);

	private DiskDriveControllerPanel panel;

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
				this.state.arm.setMagnet(q,on);
				this.state.disk.setTrack(this.state.arm.getTrack());
				this.panel.updateDrives();
			break;
			case 4:
				this.state.disk.setMotorOn(on);
				this.panel.updateDrives();
			break;
			case 5:
				this.state.disk.setDrive2(on);
				this.panel.updateDrives();
			break;
			case 6:
				if (on && this.state.write)
				{
					this.state.disk.set(data);
					this.panel.updateDrives();
				}
				else if (!(on || this.state.write))
				{
					ret = this.state.disk.get();
				}
			break;
			case 7:
				this.state.write = on;
				if (this.state.disk.isWriteProtected())
				{
					ret |= 0x80;
				}
				else
				{
					ret &= 0x7F;
				}
				this.panel.updateDrives();
			break;
		}
		return ret;
	}

	@Override
	public void reset()
	{
		this.state.disk.setMotorOn(false);
		this.state.disk.setDrive2(false);
		this.panel.updateDrives();
	}

	public void loadDisk(int drive, File fnib) throws IOException, InvalidDiskImage
	{
		this.state.loadDisk(drive,fnib);
	}

	public boolean isMotorOn()
	{
		return this.state.isMotorOn();
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
		if (disk == 0)
			return this.disk1;
		return this.disk2;
	}

}
