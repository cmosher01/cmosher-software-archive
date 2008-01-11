/*
 * Created on Sep 18, 2007
 */
package gui;

import gui.buttons.HiliteButton;
import gui.buttons.LED;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import cards.disk.DiskBytes;
import util.HexUtil;

public class DiskDrivePanel extends JPanel
{
	final GUI gui;
	private volatile boolean current;
	private volatile int track;
	private volatile boolean modified;
	private volatile boolean reading;
	private volatile boolean writing;
	private volatile boolean writeProtected;
	private final DiskDriveControllerPanel controller;
	private final DiskBytes drive;

	private JLabel labelTrack;
	private JLabel labelFile;
	private HiliteButton btnLoad;
	private HiliteButton btnSave;
	private LED ledRead;
	private LED ledWrite;

	private volatile boolean upd;

	public DiskDrivePanel(final DiskDriveControllerPanel controller, final DiskBytes drive, final GUI gui)
	{
		this.controller = controller;
		this.drive = drive;
		this.gui = gui;
		setOpaque(true);
		setPreferredSize(new Dimension(84,45));
		setBorder(BorderFactory.createLoweredBevelBorder());
		addNotify();

		setFocusable(false);

		setupControls();
	}

	private void setupControls()
	{
		setLayout(null);

		this.setBackground(Color.WHITE);
		this.labelTrack = new JLabel();
		add(this.labelTrack);
		this.labelTrack.setBounds(4,2,35,16);
		this.labelTrack.setFont(new Font("Arial",Font.PLAIN,10));
		this.labelTrack.setFocusable(false);

		this.ledRead = new LED("R",Color.GREEN,20,10);
		add(this.ledRead);
		Dimension sz = this.ledRead.getPreferredSize();
		this.ledRead.setBounds(35,5,(int)sz.getWidth(),(int)sz.getHeight());
		this.ledRead.setFocusable(false);

		this.ledWrite = new LED("W",Color.RED,20,10);
		add(this.ledWrite);
		sz = this.ledWrite.getPreferredSize();
		this.ledWrite.setBounds(60,5,(int)sz.getWidth(),(int)sz.getHeight());
		this.ledWrite.setFocusable(false);

		this.labelFile = new JLabel();
		add(this.labelFile);
		this.labelFile.setBounds(2,14,78,16);
		this.labelFile.setFont(new Font("Arial",Font.PLAIN,10));
		this.labelFile.setFocusable(false);

		this.btnLoad = new HiliteButton("unload",42,12);
		add(this.btnLoad);
		sz = this.btnLoad.getPreferredSize();
		this.btnLoad.setBounds(4,30,(int)sz.getWidth(),(int)sz.getHeight());
		this.btnLoad.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e)
			{
				openFile();
			}
		});
		this.btnLoad.setFocusable(false);

		this.btnSave = new HiliteButton("save",30,12);
		add(this.btnSave);
		sz = this.btnSave.getPreferredSize();
		this.btnSave.setBounds(50,30,(int)sz.getWidth(),(int)sz.getHeight());
		this.btnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e)
			{
				saveFile();
			}
		});
		this.btnSave.setFocusable(false);

		setUpDrop();

		this.upd = true;
		updateIf();
	}

	private void setUpDrop()
	{
		this.dropListener = new DropTargetAdapter()
        {
			public void drop(final DropTargetDropEvent evt)
			{
                final Transferable tr = evt.getTransferable();
                if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);

                    final List<File> files = getFileList(tr);

                    evt.getDropTargetContext().dropComplete(true);

                    if (files.size() > 0)
                    {
                    	openDroppedFile(files.get(0));
                    }
                    DiskDrivePanel.this.gui.toFront();
                }
			}

			@SuppressWarnings("unchecked") /* DataFlavor.javaFileListFlavor always results in List<File> */
			private List<File> getFileList(final Transferable tr)
			{
				List<File> fileList = new ArrayList<File>();
				try
				{
					fileList = (List<File>)tr.getTransferData(DataFlavor.javaFileListFlavor);
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
				return fileList;
			}
        };

        new DropTarget(this,this.dropListener);
    }

	void saveFile()
	{
		this.upd = true;
		try
		{
			this.drive.save();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.gui.showMessage(e.getMessage());
		}
		this.controller.updateDrives();
		this.btnSave.mouseExited();
	}

	void openFile()
	{
		// TODO better error handling/recovery for opening disk files
		this.upd = true;
		if (this.drive.isLoaded())
		{
			try
			{
				if (this.drive.isModified())
				{
					this.gui.askLoseUnsavedChanges();
				}
				this.drive.unload();
			}
			catch (final UserCancelled e)
			{
				// OK, user cancelled, so don't unload the disk
			}
		}
		else
		{
			try
			{
				final File file = this.gui.getFileToOpen(null);
				this.drive.load(file);
			}
			catch (final UserCancelled e1)
			{
				// user cancelled, so do nothing
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				this.gui.showMessage(e.getMessage());
			}
		}
		this.controller.updateDrives();
		this.btnLoad.mouseExited();
	}

	public void updateIf()
	{
		updateIf(false);
	}

	public void updateIf(boolean force)
	{
		if (!this.upd && !force)
		{
			return;
		}
		try
		{
			if (SwingUtilities.isEventDispatchThread())
			{
				updateEvent();
			}
			else
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						updateEvent();
					}
				});
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	private StringBuilder sb = new StringBuilder(4);
	private DropTargetListener dropListener;

	void updateEvent()
	{
		this.sb.setLength(0);
		this.sb.append("T$");
		this.sb.append(HexUtil.byt((byte)this.track));
		this.labelTrack.setText(this.sb.toString());
		this.labelTrack.setVisible(this.current);
		this.ledRead.setVisible(this.current);
		this.ledWrite.setVisible(this.current);
		this.labelFile.setText(this.drive.getFileName());
		this.btnSave.setVisible(!this.writeProtected);
		this.btnSave.setEnabled(!this.writeProtected && this.modified);
		this.btnLoad.setText(this.drive.isLoaded() ? "unload" : "load");
		repaint();
		this.upd = false;
	}
	
	/**
	 * @param modified the modified to set
	 */
	public void setModified(boolean modified)
	{
		if (this.modified != modified)
		{
			this.modified = modified;
			this.upd = true;
		}
	}

	/**
	 * @param reading the reading to set
	 */
	public void setReading(boolean reading)
	{
		if (this.reading != reading)
		{
			this.reading = reading;
			this.ledRead.setOn(reading);
			this.upd = true;
		}
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(int track)
	{
		if (this.track != track)
		{
			this.track = track;
			this.upd = true;
		}
	}

	/**
	 * @param writing the writing to set
	 */
	public void setWriting(boolean writing)
	{
		if (this.writing != writing)
		{
			this.writing = writing;
			this.ledWrite.setOn(writing);
			this.upd = true;
		}
	}

	void openDroppedFile(final File f)
	{
		this.upd = true;
		if (this.drive.isLoaded())
		{
			try
			{
				if (this.drive.isModified())
				{
					this.gui.askLoseUnsavedChanges();
				}
				this.drive.unload();
			}
			catch (final UserCancelled e)
			{
				// OK, user cancelled, so don't unload the disk
			}
		}
		try
		{
			this.drive.load(f);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			this.gui.showMessage(e.getMessage());
		}
		this.controller.updateDrives();
	}

	public void setWriteProtected(boolean writeProtected)
	{
		if (this.writeProtected != writeProtected)
		{
			this.writeProtected = writeProtected;
			this.upd = true;
		}
	}

	public DropTargetListener getDropListener()
	{
		return this.dropListener;
	}

	public void setCurrent(boolean current)
	{
		if (this.current != current)
		{
			this.current = current;
			this.upd = true;
		}
	}
}
