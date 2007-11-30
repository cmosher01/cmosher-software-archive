package gui;

import gui.buttons.DiskDrivePanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.dnd.DropTargetListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import disk.DiskBytes;
import disk.DiskState;

/*
 * Created on Sep 19, 2007
 */
class ContentPane extends JPanel
{
	private final Screen screen;
	private final DiskState disk;
	private DiskDrivePanel diskDrive1;
	private DiskDrivePanel diskDrive2;
	

	public ContentPane(final Screen screen, final DiskBytes drive1, final DiskBytes drive2, final FrameManager framer, final DiskState disk)
	{
		this.screen = screen;
		this.disk = disk;
		setOpaque(true);
		addNotify();

		setUp(drive1,drive2,framer);
	}

	private void setUp(final DiskBytes drive1, final DiskBytes drive2, final FrameManager framer)
	{
		setLayout(null);
		//setBackground(Color.BLACK);

		setBorder(BorderFactory.createLoweredBevelBorder());

		add(this.screen);
		Dimension szVideo = this.screen.getPreferredSize();
		Insets insets = getInsets();
		this.screen.setBounds(insets.left,insets.top,szVideo.width,szVideo.height);

		this.diskDrive1 = new DiskDrivePanel(drive1,framer);
		add(this.diskDrive1);
		Dimension szDisk = this.diskDrive1.getPreferredSize();
		this.diskDrive1.setBounds(insets.left,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());

		this.diskDrive2 = new DiskDrivePanel(drive2,framer);
		add(this.diskDrive2);
		this.diskDrive2.setBounds(insets.left+(int)szDisk.getWidth()+3,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());

		setPreferredSize(new Dimension(szVideo.width+insets.left+insets.right,szVideo.height+szDisk.height+insets.top+insets.bottom));
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

	public DropTargetListener getFirstDrivePanelDropListener()
	{
		return this.diskDrive1.getDropListener();
	}

	public void updateScreen(final Image image)
	{
		this.screen.plot(image);
	}

	public void updateDrives()
	{
		final DiskDrivePanel drive = getDrive(this.disk.getCurrentDriveNumber());
		if (drive == null)
		{
			return;
		}

		drive.setCurrent(true);
		drive.setModified(true); // TODO always allow saves
		drive.setTrack(this.disk.getTrack());
		drive.setWriteProtected(this.disk.isWriteProtected());
		if (this.disk.isMotorOn())
		{
			if (this.disk.isWriting())
			{
				drive.setWriting(true);
				drive.setReading(false);
			}
			else
			{
				drive.setWriting(false);
				drive.setReading(true);
			}
		}
		else
		{
			drive.setWriting(false);
			drive.setReading(false);
		}
		drive.updateIf();

		final DiskDrivePanel driveOther = getDrive(this.disk.getOtherDriveNumber());
		if (driveOther == null)
		{
			return;
		}
		drive.setCurrent(false);
		driveOther.setWriting(false);
		driveOther.setReading(false);

		driveOther.setModified(true); // TODO always allow saves

		driveOther.updateIf();
	}
}
