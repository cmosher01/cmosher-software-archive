package gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import chipset.Slots;



public class GUI implements UI
{
	private final JFrame frame;
	private final ContentPane contentPane;

	public GUI(final Closeable app, final Screen screen, final ComputerControlPanel compControls, final MonitorControlPanel monitorControls, final Slots slots)
	{
		setLookAndFeel();

		setDecorated();

		// Create the window.
        this.frame = new JFrame();

        // If the user clicks the close box, we call the WindowListener
        // that's passed in by the caller (who is responsible for calling
        // our close method if he determines it is OK to terminate the app)
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter()
    	{
			@Override
			public void windowClosing(@SuppressWarnings("unused") final WindowEvent e)
			{
				try
				{
					verifyLoseUnsaveChanges();
				}
				catch (UserCancelled e2)
				{
					return;
				}

				/*
				 * When the user closes the main frame, we exit the application.
				 * To do this, we dispose the frame (which ends the event dispatch
				 * thread), then we tell the app to close itself.
				 */
				final Thread th = new Thread(new Runnable()
				{
					@SuppressWarnings("synthetic-access")
					public void run()
					{
						GUI.this.frame.dispose();
					}
				});
				th.setName("User-GUI-dispose frame");
				th.setDaemon(true);
				th.start();
				try
				{
					app.close();
				}
				catch (final IOException e1)
				{
					e1.printStackTrace();
				}
			}
    	});

        this.frame.setIconImage(getFrameIcon());

        this.frame.setTitle("Apple ][");

        this.frame.setResizable(false);
//        this.frame.setJMenuBar(factoryMenuBar.createMenuBar());

        // Create and set up the content pane.
        this.contentPane = new ContentPane(screen,compControls,monitorControls,slots,this);
        this.frame.setContentPane(this.contentPane);

        new DropTarget(this.frame,this.contentPane.getFirstDrivePanelDropListener());

        // Set the window's size and position.
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);

        // Display the window.
        this.frame.setVisible(true);
	}

	public void verifyLoseUnsaveChanges() throws UserCancelled
	{
		if (this.contentPane.hasUnsavedChanges())
		{
			askLoseUnsavedChanges();
		}
	}

	public void askLoseUnsavedChanges() throws UserCancelled
	{
		if (!askOK("There are unsaved disk changes that will be LOST. Is it OK to DISCARD all disk changes?"))
		{
			throw new UserCancelled();
		}
	}

	private static void setLookAndFeel()
    {
		try
		{
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final Throwable e)
		{
			throw new IllegalStateException(e);
		}
    }

    private static void setDecorated()
    {
        // Use look and feel's (not OS's) decorations.
        // Must be done before creating any JFrame or JDialog
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    private Image getFrameIcon()
    {
        final int w = 10;
        final int h = 11;
        final int pix[] = new int[w * h];

        final int colorLine = Color.WHITE.getRGB();
        final int colorBack = Color.BLACK.getRGB();
        Arrays.fill(pix,colorBack);
        for (int x = 0; x < 4; ++x)
        {
        	pix[x+20] = colorLine;
        	pix[x+26] = colorLine;
        	pix[x+80] = colorLine;
        	pix[x+86] = colorLine;
        }
        for (int x = 0; x < 2; ++x)
        {
        	pix[x+32] = colorLine;
        	pix[x+36] = colorLine;
        	pix[x+42] = colorLine;
        	pix[x+46] = colorLine;
        	pix[x+52] = colorLine;
        	pix[x+56] = colorLine;
        	pix[x+62] = colorLine;
        	pix[x+66] = colorLine;
        	pix[x+72] = colorLine;
        	pix[x+76] = colorLine;
        }
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w,h,pix,0,w));
    }



    public void repaint()
	{
		this.frame.repaint();
	}

	public File getFileToOpen(final File initial) throws UserCancelled
	{
	    final JFileChooser chooser = new JFileChooser(initial);
	    final int actionType = chooser.showOpenDialog(this.frame);
	    if (actionType != JFileChooser.APPROVE_OPTION)
	    {
	    	throw new UserCancelled();
	    }

		return chooser.getSelectedFile();
	}

	public File getFileToSave(final File initial) throws UserCancelled
	{
	    final JFileChooser chooser = new JFileChooser(initial);
	    final int actionType = chooser.showSaveDialog(this.frame);
	    if (actionType != JFileChooser.APPROVE_OPTION)
	    {
	    	throw new UserCancelled();
	    }

		return chooser.getSelectedFile();
	}

	public void showMessage(final String message)
	{
		JOptionPane.showMessageDialog(this.frame,message);
	}

	public boolean askOK(final String message)
	{
		final int choice = JOptionPane.showConfirmDialog(this.frame,message,"Confirm",JOptionPane.OK_CANCEL_OPTION);
		return choice == JOptionPane.OK_OPTION;
	}

	public void toFront()
	{
		this.frame.toFront();
	}



	public void updateScreen()
	{
		if (this.contentPane == null)
		{
			return;
		}
		this.contentPane.updateScreen();
	}
}
