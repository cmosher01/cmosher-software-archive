/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.ja2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

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
        // Must be done before creating the JFrame.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the window.
        mFrame = new JFrame();

        // Closing the window exits the program.
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mFrame.setIconImage(getFrameIcon());

        // Create and set up the content pane.
        mFrame.setContentPane(createContentPane());

        // Create and set up the menu bar.
        mFrame.setJMenuBar(createMenuBar());

        // Set the window's size and position.
        mFrame.pack();
        mFrame.setLocationRelativeTo(null);
        mFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        // Display the window.
        mFrame.setVisible(true);
    }

    /**
     * @return image to use as the main frame's icon
     */
    protected Image getFrameIcon()
    {
        return new ImageIcon(this.getClass().getResource("appicon.gif")).getImage();
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
//        JMenuBar mb = new JMenuBar();
//        return mb;
        return null;
    }

    /**
     * @return main content pane for the application
     */
    protected JPanel createContentPane()
    {
        String[] columnNames = {"First Name", "Last Name", "Sport", "# of Years", "Vegetarian"};

        Object[][] data = { {"Mary", "Campione", "Snowboarding", new Integer(5), new Boolean(false)},
                {"Alison", "Huml", "Rowing", new Integer(3), new Boolean(true)},
                {"Kathy", "Walrath", "Knitting", new Integer(2), new Boolean(false)},
                {"Sharon", "Zakhour", "Speed reading", new Integer(20), new Boolean(true)},
                {"Philip", "Milne", "Pool", new Integer(10), new Boolean(false)}};

        JTable table = new JTable(data,columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(640,480));

        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel panel = new JPanel(new BorderLayout(),true);
        panel.setOpaque(true);
        panel.addNotify();
        panel.add(scrollpane,BorderLayout.CENTER);

        return panel;
    }
}
