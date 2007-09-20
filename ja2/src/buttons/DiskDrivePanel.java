/*
 * Created on Sep 18, 2007
 */
package buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import other.HexUtil;

public class DiskDrivePanel extends JPanel
{
	private volatile String file;
	private volatile int track;
	private volatile boolean modified;
	private volatile boolean reading;
	private volatile boolean writing;

	private JLabel labelTrack;
	private JLabel labelFile;
	private HiliteButton btnLoad;
	private HiliteButton btnSave;
	private LED ledRead;
	private LED ledWrite;

	public DiskDrivePanel()
	{
		setOpaque(true);
		setPreferredSize(new Dimension(81,43));
//		setBorder(BorderFactory.createLoweredBevelBorder());
		addNotify();

		setupControls();
	}

	private void setupControls()
	{
		setLayout(null);

		this.setBackground(Color.WHITE);
		this.labelTrack = new JLabel();
		add(this.labelTrack);
		this.labelTrack.setBounds(2,2,35,16);
		this.labelTrack.setFont(new Font("Arial",Font.PLAIN,10));

		this.ledRead = new LED("R",Color.GREEN,20,10);
		add(this.ledRead);
		Dimension sz = this.ledRead.getPreferredSize();
		this.ledRead.setBounds(35,5,(int)sz.getWidth(),(int)sz.getHeight());

		this.ledWrite = new LED("W",Color.RED,20,10);
		add(this.ledWrite);
		sz = this.ledWrite.getPreferredSize();
		this.ledWrite.setBounds(60,5,(int)sz.getWidth(),(int)sz.getHeight());

		this.labelFile = new JLabel();
		add(this.labelFile);
		this.labelFile.setBounds(2,14,78,16);

		this.btnLoad = new HiliteButton("unload",42,12);
		add(this.btnLoad);
		sz = this.btnLoad.getPreferredSize();
		this.btnLoad.setBounds(2,30,(int)sz.getWidth(),(int)sz.getHeight());
		this.btnLoad.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (file == null)
				{
					
				}
				else
				{
					
				}
			}
		});

		this.btnSave = new HiliteButton("save",30,12);
		add(this.btnSave);
		sz = this.btnSave.getPreferredSize();
		this.btnSave.setBounds(50,30,(int)sz.getWidth(),(int)sz.getHeight());

		//update();
	}

	public void update()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					DiskDrivePanel.this.labelTrack.setText("T$"+HexUtil.byt((byte)DiskDrivePanel.this.track));
					DiskDrivePanel.this.labelFile.setText(DiskDrivePanel.this.file);
					DiskDrivePanel.this.btnSave.setEnabled(DiskDrivePanel.this.modified);
					DiskDrivePanel.this.btnLoad.setText(DiskDrivePanel.this.file == null ? "load" : "unload");
				}
			});
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		repaint();
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file)
	{
		this.file = file;
		update();
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(boolean modified)
	{
		this.modified = modified;
		update();
	}

	/**
	 * @param reading the reading to set
	 */
	public void setReading(boolean reading)
	{
		this.reading = reading;
		update();
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(int track)
	{
		this.track = track;
		update();
	}

	/**
	 * @param writing the writing to set
	 */
	public void setWriting(boolean writing)
	{
		this.writing = writing;
		update();
	}

}
