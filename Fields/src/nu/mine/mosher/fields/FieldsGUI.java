/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.ja2;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

/**
 * @author Chris Mosher
 */
public class GUI
{
    private ContentPane mPaneContent;
    private ContentMenu mJMenuBar;
    private JFrame mFrame;

    public GUI(ContentPane paneContent, ContentMenu jMenuBar)
    {
        this.mPaneContent = paneContent;
        this.mJMenuBar = jMenuBar;

        if (this.mPaneContent == null)
        {
            throw new IllegalStateException("GUI requires a ContentPane.");
        }
    }

    /**
     * 
     */
    public void create()
    {
        // Use look and feel for current OS.
        //SwingUtil.useOSLookAndFeel();

        // Use look and feel's (not OS's) decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the window.
        mFrame = new JFrame();

        // Closing the window exits the program.
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        mPaneContent.create();
        mFrame.setContentPane(mPaneContent);

        // Create and set up the menu bar.
        mJMenuBar.create();
        mFrame.setJMenuBar(mJMenuBar);

        // Set the window's size and position.
        mFrame.pack();
        mFrame.setLocationRelativeTo(null);

        // Display the window.
        mFrame.setVisible(true);
    }
}
