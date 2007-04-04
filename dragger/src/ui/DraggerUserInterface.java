/*
 * Created on Apr 19, 2006
 */
package ui;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;



public class DraggerUserInterface implements Closeable
{
    private JFrame frame;
    private DragPanel panelDrag;

    private static void setSwingDefaults()
    {
        setLookAndFeel();

        // Use look and feel's (not OS's) decorations.
        // Must be done before creating any JFrame or JDialog
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        setDefaultFont();
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



    public void init()
    {
    	setSwingDefaults();

    	// Create the window.
        this.frame = new JFrame();

        // If the user clicks the close box, we call the WindowListener
        // that's passed in by the caller (who is responsible for calling
        // our close method if he determines it is OK to terminate the app)
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                close();
            }
        });

        //this.frame.setIconImage(getFrameIcon());

        this.frame.setTitle("Testing my app");

        this.frame.setJMenuBar(buildMenuBar());

        this.panelDrag = new DragPanel();

        // Create and set up the content pane.
        this.frame.setContentPane(new JScrollPane(panelDrag));

        // Set the window's size and position.
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);

        // Display the window.
        this.frame.setVisible(true);
    }

    public void close()
    {
        this.frame.dispose();
    }

    private JMenuBar buildMenuBar()
    {
        final JMenuBar bar = new JMenuBar();

        final JMenu menuFile = new JMenu("File");
        final JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(final ActionEvent e)
				{
					close();
				}
			});
        menuFile.add(itemExit);

        bar.add(menuFile);

        return bar;
    }


	public void test(List<DrawablePersona> rPersona, List<DrawableFamily> rFamily)
	{
        this.panelDrag.set(rPersona,rFamily);
        this.panelDrag.calc();
        this.panelDrag.repaint();
	}
}
