/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.ja2;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import nu.mine.mosher.javax.swing.SwingUtil;

/**
 * @author Chris Mosher
 */
public class GUI
{
    private JFrame mFrame;

    /**
     * 
     */
    public void create()
    {
        // Use look and feel for current OS.
        setLookAndFeel();

        // Use look and feel's (not OS's) decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the window.
        mFrame = new JFrame();

        // Closing the window exits the program.
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        mFrame.setContentPane(createContentPane());

        // Create and set up the menu bar.
        mFrame.setJMenuBar(createMenuBar());

        // Set the window's size and position.
        mFrame.pack();
        mFrame.setLocationRelativeTo(null);

        // Display the window.
        mFrame.setVisible(true);
    }

    /**
     * 
     */
    private void setLookAndFeel()
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
     * @return
     */
    private JMenuBar createMenuBar()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return
     */
    private JPanel createContentPane()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
