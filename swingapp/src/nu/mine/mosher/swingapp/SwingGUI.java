/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.ja2;

import javax.swing.JFrame;

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
        SwingUtil.useOSLookAndFeel();

        // Use look and feel's (not OS's) decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the window.
        mFrame = new JFrame("Metadata Viewer");

        // Closing the window exits the program.
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        mFrame.setContentPane(new MetadataViewerContentPane());

        // Set the window's size and position.
        mFrame.pack();
        mFrame.setLocationRelativeTo(null);

        // Display the window.
        mFrame.setVisible(true);
    }
}
