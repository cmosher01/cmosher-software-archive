/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.image.MemoryImageSource;
import java.io.Closeable;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import nu.mine.mosher.gedcom.viewer.gui.exception.UserCancelled;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

public class FrameManager implements Closeable
{
	private final GedcomTreeModel model;
	private JFrame frame;

	public FrameManager(final GedcomTreeModel model)
	{
		this.model = model;
	}

	public void init(final JMenuBar bar, final WindowListener listenerWindow)
	{
        // Create the window.
        this.frame = new JFrame();

        // If the user clicks the close box, we call the WindowListener
        // that's passed in by the caller (who is responsible for calling
        // our close method if he determines it is OK to terminate the app)
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(listenerWindow);

        this.frame.setIconImage(getFrameIcon());

        this.frame.setTitle("GEDCOM Viewer");

        this.frame.setJMenuBar(bar);

        // Create and set up the content pane.
        this.frame.setContentPane(new GedcomTreePane(this.model));

        // Set the window's size and position.
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        //mFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        // Display the window.
        this.frame.setVisible(true);

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

	public void showMessage(final String message)
	{
		JOptionPane.showMessageDialog(this.frame,message);
	}

	public void close()
	{
		this.frame.dispose();
	}


    private static Image getFrameIcon()
    {
        final int w = 100;
        final int h = 100;
        final int pix[] = new int[w * h];

        final int colorLine = Color.CYAN.getRGB();
        final int colorBack = Color.WHITE.getRGB();
        int index = 0;
        for (int y = 0; y < h; y++)
        {
    		final boolean yLine = y % 5 == 0;
            for (int x = 0; x < w; x++)
            {
        		final boolean xLine = x % 5 == 0;
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
}