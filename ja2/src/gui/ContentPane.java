package gui;

import gui.buttons.DiskDrivePanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.dnd.DropTargetListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import chipset.Card;
import chipset.Slots;
import disk.DiskBytes;
import disk.DiskState;

/*
 * Created on Sep 19, 2007
 */
class ContentPane extends JPanel
{
	private final Screen screen;
	private final Slots slots;
	private DiskDriveControllerPanel diskController;
//	private DiskDrivePanel diskDrive1;
//	private DiskDrivePanel diskDrive2;
	

	public ContentPane(final Screen screen, final Slots slots, final GUI gui)
	{
		this.screen = screen;
		this.slots = slots;
		setOpaque(true);
		addNotify();

		setUp(gui);
	}

	private void setUp(final GUI gui)
	{
		setLayout(null);

		setBorder(BorderFactory.createLoweredBevelBorder());

		add(this.screen);
		Dimension szVideo = this.screen.getPreferredSize();
		Insets insets = getInsets();
		this.screen.setBounds(insets.left,insets.top,szVideo.width,szVideo.height);

		this.diskController = new DiskDriveControllerPanel(controller,gui);
		this.diskController.setBounds(insets.left,szVideo.height+2,(int)this.diskController.getWidth(),(int)this.diskController.getHeight());

//		this.diskDrive1 = new DiskDrivePanel(drive1,gui);
//		add(this.diskDrive1);
//		Dimension szDisk = this.diskDrive1.getPreferredSize();
//		this.diskDrive1.setBounds(insets.left,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());
//
//		this.diskDrive2 = new DiskDrivePanel(drive2,gui);
//		add(this.diskDrive2);
//		this.diskDrive2.setBounds(insets.left+(int)szDisk.getWidth()+3,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());

		setPreferredSize(new Dimension(szVideo.width+insets.left+insets.right,szVideo.height+szDisk.height+insets.top+insets.bottom));
	}

//	private DiskDrivePanel getDrive(int drive)
//	{
//		if (drive == 0)
//		{
//			return this.diskDrive1;
//		}
//		if (drive == 1)
//		{
//			return this.diskDrive2;
//		}
//		throw new IllegalStateException();
//	}

	public DropTargetListener getFirstDrivePanelDropListener()
	{
		return this.diskDrive1.getDropListener();
	}

	public void updateScreen(final Image image)
	{
		this.screen.plot(image);
	}

	public void updateDrives(boolean force)
	{
		this.diskController.updateDrives(force);
//		final DiskDrivePanel drivePanelCurrent = getDrive(this.disk.getCurrentDriveNumber());
//		if (drivePanelCurrent == null)
//		{
//			return;
//		}
//
//		drivePanelCurrent.setCurrent(true);
//		drivePanelCurrent.setModified(this.disk.isModified());
//		drivePanelCurrent.setTrack(this.disk.getTrack());
//		drivePanelCurrent.setWriteProtected(this.disk.isWriteProtected());
//		if (this.disk.isMotorOn())
//		{
//			if (this.disk.isWriting())
//			{
//				drivePanelCurrent.setWriting(true);
//				drivePanelCurrent.setReading(false);
//			}
//			else
//			{
//				drivePanelCurrent.setWriting(false);
//				drivePanelCurrent.setReading(true);
//			}
//		}
//		else
//		{
//			drivePanelCurrent.setWriting(false);
//			drivePanelCurrent.setReading(false);
//		}
//		drivePanelCurrent.updateIf(force);
//
//		final DiskDrivePanel drivePanelOther = getDrive(this.disk.getOtherDriveNumber());
//		if (drivePanelOther == null)
//		{
//			return;
//		}
//		drivePanelOther.setCurrent(false);
//		drivePanelOther.setWriting(false);
//		drivePanelOther.setReading(false);
//
//		drivePanelOther.setModified(this.disk.isModifiedOther());
//
//		drivePanelOther.updateIf(force);
	}

	public boolean hasUnsavedChanges()
	{
		return this.disk.isModified() || this.disk.isModifiedOther();
	}
}
