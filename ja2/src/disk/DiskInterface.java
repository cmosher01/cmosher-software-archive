package disk;

import chipset.Card;
import gui.GUI;

/*
 * Created on Sep 12, 2007
 */
public class DiskInterface implements Card
{
	private final DiskState state;
	private final GUI gui;

	/**
	 * @param disk
	 * @param manager 
	 * @param motor
	 */
	public DiskInterface(final DiskState state, final GUI gui)
	{
		this.state = state;
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
				this.state.arm.setMagnet(q,on);
				this.state.disk.setTrack(this.state.arm.getTrack());
				updatePanel();
			break;
			case 4:
				this.state.disk.setMotorOn(on);
				updatePanel();
			break;
			case 5:
				this.state.disk.setDrive2(on);
				updatePanel();
			break;
			case 6:
				if (on && this.state.write)
				{
					this.state.disk.set(data);
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
				updatePanel();
			break;
		}
		return ret;
	}

	public void reset()
	{
		this.state.disk.setMotorOn(false);
		this.state.disk.setDrive2(false);
		updatePanel();
	}

	private void updatePanel()
	{
		this.gui.updateDrives();
	}
}
