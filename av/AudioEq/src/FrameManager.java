import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.image.MemoryImageSource;
import java.io.Closeable;
import java.io.File;
import java.util.Enumeration;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

public class FrameManager implements Closeable
{
	private JFrame frame;

	public void init(final MenuBarFactory factoryMenuBar, final WindowListener listenerWindow)
	{
		setLookAndFeel();

		setDecorated();

		setDefaultFont();

		// Create the window.
        this.frame = new JFrame();

        // If the user clicks the close box, we call the WindowListener
        // that's passed in by the caller (who is responsible for calling
        // our close method if he determines it is OK to terminate the app)
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(listenerWindow);

        this.frame.setIconImage(getFrameIcon());

        this.frame.setTitle("AudioEq");

        this.frame.setJMenuBar(factoryMenuBar.createMenuBar());

        // Create and set up the content pane.
        this.frame.setContentPane(new AudioEqPane());

        // Set the window's size and position.
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);

        // Display the window.
        this.frame.setVisible(true);
	}

    private static void setLookAndFeel()
    {
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
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

    private static void setDefaultFont()
	{
		/*
		 * Use Java's platform independent font, Lucida Sans, plain, at 12 points,
		 * as the default for every Swing component.
		 */

		final FontUIResource font = new FontUIResource("Lucida Sans",Font.PLAIN,12);

		final Enumeration<Object> iterKeys = UIManager.getDefaults().keys();
    	while (iterKeys.hasMoreElements())
		{
    		final Object key = iterKeys.nextElement();
			if (UIManager.get(key) instanceof FontUIResource)
			{
				UIManager.put(key,font);
			}
		}
	}

    private Image getFrameIcon()
    {
        final int w = 100;
        final int h = 100;
        final int pix[] = new int[w * h];

        final int colorLine = Color.ORANGE.getRGB();
        final int colorBack = Color.WHITE.getRGB();
        int index = 0;
        for (int y = 0; y < h; y++)
        {
    		final boolean yLine = /*(y < 8) ||*/ (29 < y && y < 37) || (62 < y && y < 70) /*|| (92 < y)*/;
            for (int x = 0; x < w; x++)
            {
        		final boolean xLine = /*(x < 8) ||*/ (29 < x && x < 37) || (62 < x && x < 70) /*|| (92 < x)*/;
            	int color;
        		if (xLine || yLine)
            	{
            		color = colorLine;
            	}
        		else
        		{
        			color = colorBack;
        		}
                pix[index++] = color;
            }
        }
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w,h,pix,0,w));
    }



    public void repaint()
	{
		this.frame.repaint();
	}

//	public File getFileToOpen(final File initial) throws UserCancelled
//	{
//	    final JFileChooser chooser = new JFileChooser(initial);
//	    final int actionType = chooser.showOpenDialog(this.frame);
//	    if (actionType != JFileChooser.APPROVE_OPTION)
//	    {
//	    	throw new UserCancelled();
//	    }
//
//		return chooser.getSelectedFile();
//	}
//
//	public File getFileToSave(final File initial) throws UserCancelled
//	{
//	    final JFileChooser chooser = new JFileChooser(initial);
//	    final int actionType = chooser.showSaveDialog(this.frame);
//	    if (actionType != JFileChooser.APPROVE_OPTION)
//	    {
//	    	throw new UserCancelled();
//	    }
//
//		return chooser.getSelectedFile();
//	}
//
//	public void showMessage(final String message)
//	{
//		JOptionPane.showMessageDialog(this.frame,message);
//	}
//
//	public String getBoardStringFromUser() throws UserCancelled
//	{
//		NewBoardEntry entry = null;
//		try
//		{
//			entry = new NewBoardEntry(this.frame);
//			return entry.ask();
//		}
//		finally
//		{
//			if (entry != null)
//			{
//				entry.dispose();
//			}
//		}
//	}
//
//	public boolean askOK(final String message)
//	{
//		final int choice = JOptionPane.showConfirmDialog(this.frame,message,"Confirm",JOptionPane.OK_CANCEL_OPTION);
//		return choice == JOptionPane.OK_OPTION;
//	}

	public void close()
	{
		this.frame.dispose();
	}
}
