package gui;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import disk.DiskBytes;
import video.Video;
import buttons.DiskDrivePanel;

/*
 * Created on Sep 19, 2007
 */
public class ContentPane extends JPanel
{
	private Video video;
	private DiskDrivePanel diskDrive1;
	private DiskDrivePanel diskDrive2;
	

	public ContentPane(final Video video, final DiskBytes drive1, final DiskBytes drive2, final FrameManager framer)
	{
		this.video = video;
		setOpaque(true);
		addNotify();

		setUp(drive1,drive2,framer);
	}

	private void setUp(final DiskBytes drive1, final DiskBytes drive2, final FrameManager framer)
	{
		setLayout(null);
		//setBackground(Color.BLACK);

		setBorder(BorderFactory.createLoweredBevelBorder());

		add(this.video);
		Dimension szVideo = this.video.getPreferredSize();
		Insets insets = getInsets();
		this.video.setBounds(insets.left,insets.top,szVideo.width,szVideo.height);

		this.diskDrive1 = new DiskDrivePanel(drive1,framer);
		add(this.diskDrive1);
		Dimension szDisk = this.diskDrive1.getPreferredSize();
		this.diskDrive1.setBounds(insets.left,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());

		this.diskDrive2 = new DiskDrivePanel(drive2,framer);
		add(this.diskDrive2);
		this.diskDrive2.setBounds(insets.left+(int)szDisk.getWidth()+3,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());

		setPreferredSize(new Dimension(szVideo.width+insets.left+insets.right,szVideo.height+szDisk.height+insets.top+insets.bottom));
	}

	public DiskDrivePanel getDrive(int drive)
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
