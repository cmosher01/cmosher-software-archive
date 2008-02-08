import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import util.Util;
import cards.disk.InvalidDiskImage;
import chipset.InvalidMemoryLoad;
import config.Config;
import emu.CLIEmulator;
import emu.Emulator;
import emu.GUIEmulator;

/*
 * Created on 2004-08-31
 */

/**
 * Contains the "main" method (external entry point) for
 * the application. This class is in the default package
 * so that it can only be run from the command line, and
 * using the following simple format:
 * <code>java Ja2 [arguments]</code>
 * 
 * @author Chris Mosher
 */
public final class Ja2 implements Runnable
{
	static
	{
		// set the main thread's name
		Thread.currentThread().setName("User-main");
	}

	/**
	 * Runs the emulator.
	 * 
	 * @param args array of command line arguments
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void main(final String... args) throws InterruptedException, InvocationTargetException
    {
		/*
		 * Start Swing's event dispatch thread right away, and
		 * run our program on it. The main thread will wait here
		 * until we are sure the event dispatch thread is
		 * running. The program's run method will set everything
		 * up (while running on the event dispatch thread), and
		 * then return. After that, invokeAndWait will return
		 * to us, and we exit the main thread.
		 */
    	SwingUtilities.invokeAndWait(new Ja2(args));
    }



	private final List<String> args;
	private boolean gui = true;
	private String config = "ja2.cfg";



	/**
	 * Initializes this emulator.
	 * @param args command line arguments
	 */
	public Ja2(final String... args)
    {
		// note: this runs on the main thread
		this.args = Collections.<String>unmodifiableList(Arrays.<String>asList(args));
    }

	/**
	 * Runs the program. This method is called on the
	 * event dispatch thread.
	 *
	 */
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

    private void tryRun() throws IOException, InvalidMemoryLoad, InvalidDiskImage
    {
    	parseArgs();

    	final Emulator emu = this.gui ? new GUIEmulator() : new CLIEmulator();

    	final Config cfg = new Config(this.config);
    	emu.config(cfg);

    	emu.init();
    }

    private void parseArgs()
	{
		for (final String arg : this.args)
		{
			if (arg.startsWith("--"))
			{
				parseArg(arg.substring(2));
			}
			else
			{
				throw new IllegalArgumentException(arg);
			}
		}
	}

	private void parseArg(final String arg)
	{
		final StringTokenizer tok = new StringTokenizer(arg,"=");
		final String opt = Util.nextTok(tok);
		final String val = Util.nextTok(tok);

		if (opt.equals("config"))
		{
			this.config = val;
		}
		else if (opt.equals("gui"))
		{
			this.gui = true;
		}
		else if (opt.equals("cli"))
		{
			this.gui = false;
		}
		else
		{
			throw new IllegalArgumentException(arg);
		}
	}
}
