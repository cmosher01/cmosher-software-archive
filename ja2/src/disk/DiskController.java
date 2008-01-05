package disk;

import java.io.File;
import java.io.IOException;
import chipset.Card;
import gui.UI;

/*
 * Created on Sep 12, 2007
 */
public class DiskController extends Card
{
	private final DiskState state;
	private final UI ui;

	/**
	 * @param disk
	 * @param manager 
	 * @param motor
	 */
	public DiskController(final UI ui)
	{
    	final DiskBytes disk1 = new DiskBytes();
    	final DiskBytes disk2 = new DiskBytes();
    	final DiskDriveSimple drive = new DiskDriveSimple(new DiskBytes[] {disk1,disk2});
    	final StepperMotor arm = new StepperMotor();
    	this.state = new DiskState(drive,arm);
		this.ui = ui;
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
				this.state.arm.setMagnet(q,on);
				this.state.disk.setTrack(this.state.arm.getTrack());
				this.ui.updateDrives();
			break;
			case 4:
				this.state.disk.setMotorOn(on);
				this.ui.updateDrives();
			break;
			case 5:
				this.state.disk.setDrive2(on);
				this.ui.updateDrives();
			break;
			case 6:
				if (on && this.state.write)
				{
					this.state.disk.set(data);
					this.ui.updateDrives();
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
				this.ui.updateDrives();
			break;
		}
		return ret;
	}

	@Override
	public void reset()
	{
		this.state.disk.setMotorOn(false);
		this.state.disk.setDrive2(false);
		this.ui.updateDrives();
	}

	public void loadDisk(int drive, File fnib) throws IOException, InvalidDiskImage
	{
		this.state.loadDisk(drive,fnib);
	}
}
