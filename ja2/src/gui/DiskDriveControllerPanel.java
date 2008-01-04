/*
 * Created on Jan 4, 2008
 */
package gui;

import gui.buttons.DiskDrivePanel;
import java.awt.Dimension;
import javax.swing.JPanel;

public class DiskDriveControllerPanel extends JPanel
{
	private DiskDrivePanel diskDrive1;
	private DiskDrivePanel diskDrive2;

	private void setUp()
	{
		this.diskDrive1 = new DiskDrivePanel(drive1,gui);
		add(this.diskDrive1);
		Dimension szDisk = this.diskDrive1.getPreferredSize();
		this.diskDrive1.setBounds(insets.left,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());

		this.diskDrive2 = new DiskDrivePanel(drive2,gui);
		add(this.diskDrive2);
		this.diskDrive2.setBounds(insets.left+(int)szDisk.getWidth()+3,szVideo.height+2,(int)szDisk.getWidth(),(int)szDisk.getHeight());
	}
}
