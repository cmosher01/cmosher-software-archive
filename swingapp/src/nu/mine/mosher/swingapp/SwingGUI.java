/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.swingapp;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

/**
 * @author Chris Mosher
 */
public class SwingGUI
{
    protected JFrame mFrame;

    /**
     * 
     */
    public void create()
    {
        // Use look and feel for current OS.
        setLookAndFeel();

        // Use look and feel's (not OS's) decorations.
        // Must be done before creating the JFrame.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        // Create the window.
        mFrame = new JFrame();

        // Closing the window exits the program.
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mFrame.setIconImage(getFrameIcon());

        mFrame.setTitle(getTitle());

        // Create and set up the content pane.
        mFrame.setContentPane(createContentPane());

        // Create and set up the menu bar.
        mFrame.setJMenuBar(createMenuBar());

        // Set the window's size and position.
        mFrame.pack();
        mFrame.setLocationRelativeTo(null);
        //mFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        // Display the window.
        mFrame.setVisible(true);
    }

    /**
     * @return image to use as the main frame's icon
     */
    protected Image getFrameIcon()
    {
        int w = 100;
        int h = 100;
        int pix[] = new int[w * h];
        int index = 0;
        for (int y = 0; y < h; y++)
        {
            int red = (y * 255) / (h - 1);
            for (int x = 0; x < w; x++)
            {
                int blue = (x * 255) / (w - 1);
                pix[index++] = (255 << 24) | (red << 16) | blue;
            }
        }
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w,h,pix,0,w));
    }

    /**
     * 
     */
    protected void setLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Throwable e)
        {
            RuntimeException re = new IllegalStateException();
            re.initCause(e);
            throw re;
        }
    }

    /**
     * @return menu bar for the application
     */
    protected JMenuBar createMenuBar()
    {
        return null;
    }

    /**
     * @return main content pane for the application
     */
    protected JPanel createContentPane()
    {
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel panel = new JPanel(new BorderLayout(),true);
        panel.setOpaque(true);
        panel.addNotify();
        panel.add(scrollpane,BorderLayout.CENTER);

        return panel;
    }

    protected String getTitle()
    {
    	return "";
    }
}
