import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;



public class AudioEq implements Runnable, Closeable
{
	/**
	 * @param args
	 * @throws InvocationTargetException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, InvocationTargetException
	{
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			public void run()
			{
		    	final Runnable program = new AudioEq();
		    	program.run();
			}
    	}
    	);
	}

	private final FrameManager framer = new FrameManager();

	private AudioEq()
	{
		// instantiated only in main
	}

	@Override
	public void run()
	{
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
	    	});
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

//		this.filer.appendMenuItems(menuFile);
//		menuFile.addSeparator();
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

	@Override
	public void close()
	{
		this.framer.close(); // this exits the app
	}
}
