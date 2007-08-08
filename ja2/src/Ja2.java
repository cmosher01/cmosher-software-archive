import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/*
 * Created on Aug 31, 2004
 */

/**
 * Contains the "main" method (external entry point) for
 * the application. This class is in the default package
 * so that the program can be run with the following command:
 * <code>java Ja2 [arguments]</code>
 * 
 * @author Chris Mosher
 */
public final class Ja2 implements Runnable
{
	private final FrameManager framer = new FrameManager();

    private Ja2()
    {
    	// only instantiated by main
    }

	/**
     * @param args
     * @throws IOException 
     * @throws ApplicationAborting
     */
    public static void main(String[] args) throws IOException, InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			public void run()
			{
		    	final Runnable program = new Ja2();
		    	program.run();
			}
    	}
    	);
    }

    public void run()
	{
    	try
		{
			tryRun();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

    private void tryRun() throws IOException
    {
        final Memory memory = new Memory();
    	memory.write(0x400,'A'|0x80);
    	memory.write(0x401,'B'|0x80);
    	memory.write(0x402,'C'|0x80);
    	memory.write(0x403,'D'|0x80);
    	memory.write(0x404,'E'|0x80);
    	memory.write(0x405,'F'|0x80);

    	final Video video = new Video(memory);

//    	final CPU6502 cpu = new CPU6502(memory);

        // create the main frame window for the application
        this.framer.init(
        	new MenuBarFactory()
	    	{
				public JMenuBar createMenuBar()
				{
					return createAppMenuBar();
				}
	    	
	    	},
	    	new WindowAdapter()
	    	{
				@Override
				public void windowClosing(final WindowEvent e)
				{
					close();
				}
	    	},
	    	video);

    	final List<Clock.Timed> rTimed = new ArrayList<Clock.Timed>();
//    	rTimed.add(cpu);
    	rTimed.add(video);
    	final Clock clock = new Clock(rTimed);

    	final Thread clth = new Thread(new Runnable()
    	{
			public void run()
			{
		    	clock.run();
			}
    	});
    	clth.start();
	}

    private JMenuBar createAppMenuBar()
	{
		final JMenuBar menubar = new JMenuBar();
        appendMenus(menubar);
        return menubar;
	}

	private void appendMenus(final JMenuBar bar)
	{
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);

		appendMenuItems(menuFile);

		bar.add(menuFile);
	}

	private void appendMenuItems(final JMenu menu)
	{
		final JMenuItem itemFileExit = new JMenuItem("Exit");
		itemFileExit.setMnemonic(KeyEvent.VK_X);
		itemFileExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				close();
			}
		});
		menu.add(itemFileExit);
	}

	public void close()
	{
		this.framer.close(); // this exits the app
	}
}
