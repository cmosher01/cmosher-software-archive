/*
 * Created on Jan 4, 2008
 */
package gui;

import gui.buttons.DiskDrivePanel;
import java.awt.Dimension;
import javax.swing.JPanel;
import disk.DiskController;

public class DiskDriveControllerPanel extends JPanel
{
	final DiskController controller;
	final GUI gui;

	private DiskDrivePanel diskDrive1;
	private DiskDrivePanel diskDrive2;

	public DiskDriveControllerPanel(final DiskController controller, final GUI gui)
	{
		this.controller = controller;
		this.gui = gui;
	}

	private void setUp()
	{
		this.diskDrive1 = new DiskDrivePanel(this.controller.getDiskBytes(0),this.gui);
		add(this.diskDrive1);
		Dimension szDisk = this.diskDrive1.getPreferredSize();
		this.diskDrive1.setBounds(0,2,(int)szDisk.getWidth(),(int)szDisk.getHeight());

		this.diskDrive2 = new DiskDrivePanel(this.controller.getDiskBytes(1),this.gui);
		add(this.diskDrive2);
		this.diskDrive2.setBounds((int)szDisk.getWidth()+3,2,(int)szDisk.getWidth(),(int)szDisk.getHeight());
	}

	public void updateDrives(boolean force)
	{
		final DiskDrivePanel drivePanelCurrent = getDrive(this.controller.getCurrentDriveNumber());
		if (drivePanelCurrent == null)
		{
			return;
		}

		drivePanelCurrent.setCurrent(true);
		drivePanelCurrent.setModified(this.controller.isModified());
		drivePanelCurrent.setTrack(this.controller.getTrack());
		drivePanelCurrent.setWriteProtected(this.controller.isWriteProtected());
		if (this.controller.isMotorOn())
		{
			if (this.controller.isWriting())
			{
				drivePanelCurrent.setWriting(true);
				drivePanelCurrent.setReading(false);
			}
			else
			{
				drivePanelCurrent.setWriting(false);
				drivePanelCurrent.setReading(true);
			}
		}
		else
		{
			drivePanelCurrent.setWriting(false);
			drivePanelCurrent.setReading(false);
		}
		drivePanelCurrent.updateIf(force);

		final DiskDrivePanel drivePanelOther = getDrive(this.controller.getOtherDriveNumber());
		if (drivePanelOther == null)
		{
			return;
		}
		drivePanelOther.setCurrent(false);
		drivePanelOther.setWriting(false);
		drivePanelOther.setReading(false);

		drivePanelOther.setModified(this.controller.isModifiedOther());

		drivePanelOther.updateIf(force);
	}

	private DiskDrivePanel getDrive(int drive)
	{
		if (drive == 0)
		{
			return this.diskDrive1;
		}
		if (drive == 1)
		{
			return this.diskDrive2;
		}
		throw new IllegalStateException();
	}
}
