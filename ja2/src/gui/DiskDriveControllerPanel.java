/*
 * Created on Jan 4, 2008
 */
package gui;

import java.awt.FlowLayout;
import java.awt.dnd.DropTargetListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import disk.DiskController;

public final class DiskDriveControllerPanel extends JPanel
{
	final DiskController controller;
	final GUI gui;

	private DiskDrivePanel diskDrive1;
	private DiskDrivePanel diskDrive2;

	public DiskDriveControllerPanel(final DiskController controller, final GUI gui)
	{
		this.controller = controller;
		this.gui = gui;

		setOpaque(true);
		addNotify();
		setFocusable(false);

		setUp();
	}

	private void setUp()
	{
		setLayout(new FlowLayout());

		this.diskDrive1 = new DiskDrivePanel(this,this.controller.getDiskBytes(0),this.gui);
		add(this.diskDrive1);

		this.diskDrive2 = new DiskDrivePanel(this,this.controller.getDiskBytes(1),this.gui);
		add(this.diskDrive2);
	}

	public void updateDrives()
	{
		updateDrives(false);
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

	public DropTargetListener getDropListener()
	{
		return this.diskDrive1.getDropListener();
	}
}
