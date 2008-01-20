import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/*
 * Created on Jan 18, 2008
 */
public class GUI
{
	private final JFrame frame;
	private final ContentPane contentPane;

	public GUI(final Closeable app, final BufferedImage image)
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

//        this.frame.setIconImage(getFrameIcon());

        this.frame.setTitle("NTSC");

        this.frame.setResizable(false);
//        this.frame.setJMenuBar(factoryMenuBar.createMenuBar());

        // Create and set up the content pane.
        this.contentPane = new ContentPane(image);
        this.frame.setContentPane(this.contentPane);

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
}
