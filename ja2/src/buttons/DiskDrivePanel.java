/*
 * Created on Sep 18, 2007
 */
package buttons;

import gui.FrameManager;
import gui.UserCancelled;
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
import util.HexUtil;
import disk.DiskBytes;
import disk.DiskInterface;

public class DiskDrivePanel extends JPanel
{
	private final FrameManager framer;
	private final DiskInterface diskInterface;
	private volatile File file;
	private volatile String fileName = "";
	private volatile int track;
	private volatile boolean modified;
	private volatile boolean reading;
	private volatile boolean writing;
	private volatile boolean writeProtected;
	private final DiskBytes drive;

	private JLabel labelTrack;
	private JLabel labelFile;
	private HiliteButton btnLoad;
	private HiliteButton btnSave;
	private LED ledRead;
	private LED ledWrite;

	private volatile boolean upd;

	public DiskDrivePanel(final DiskBytes drive, final FrameManager framer, final DiskInterface diskInterface)
	{
		this.drive = drive;
		this.framer = framer;
		this.diskInterface = diskInterface;
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
			public void actionPerformed(ActionEvent e)
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
			public void actionPerformed(ActionEvent e)
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
                    DiskDrivePanel.this.framer.toFront();
                }
			}

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
	private void saveFile()
	{
		this.upd = true;
		if (this.file == null)
		{
			return;
		}
		try
		{
			this.drive.save(this.file);
			this.modified = false;
		}
		catch (IOException e)
		{
			this.framer.showMessage(e.getMessage());
			e.printStackTrace();
		}
		this.diskInterface.updatePanel();
		this.btnSave.mouseExited();
	}

	private void openFile()
	{
		this.upd = true;
		if (this.file == null)
		{
			try
			{
				this.file = this.framer.getFileToOpen(null);
				this.file = this.file.getCanonicalFile();
				this.fileName = this.file.getName();
				this.drive.load(this.file);
				this.modified = false;
			}
			catch (UserCancelled e1)
			{
				// user cancelled, so do nothing
			}
			catch (IOException e)
			{
				this.framer.showMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		else
		{
			this.drive.unload();
			this.file = null;
			this.fileName = "";
		}
		this.diskInterface.updatePanel();
		this.btnLoad.mouseExited();
	}

	public void updateIf()
	{
		if (!this.upd)
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

	private void updateEvent()
	{
		this.sb.setLength(0);
		this.sb.append("T$");
		this.sb.append(HexUtil.byt((byte)this.track));
		this.labelTrack.setText(this.sb.toString());
		this.labelFile.setText(this.fileName);
		this.btnSave.setVisible(!this.writeProtected);
		this.btnSave.setEnabled(!this.writeProtected && this.modified);
		this.btnLoad.setText(this.file == null ? "load" : "unload");
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

	private void openDroppedFile(final File file)
	{
		this.upd = true;
		if (this.file != null)
		{
			this.drive.unload();
			this.file = null;
			this.fileName = "";
			this.modified = false;
		}
		try
		{
			this.drive.load(file);
			this.file = file;
			this.fileName = this.file.getName();
			this.modified = false;
		}
		catch (IOException e)
		{
			this.framer.showMessage(e.getMessage());
			e.printStackTrace();
		}
		this.diskInterface.updatePanel();
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
}
