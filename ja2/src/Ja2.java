import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import util.Util;
import cards.disk.InvalidDiskImage;
import cards.stdio.StandardIn;
import chipset.InvalidMemoryLoad;
import config.Config;
import emu.Emulator;

/*
 * Created on Aug 31, 2004
 */

/**
 * Contains the "main" method (external entry point) for
 * the application. This class is in the default package
 * so that it can only be run from the command line, and
 * using the following format:
 * <code>java Ja2 [arguments]</code>
 * 
 * @author Chris Mosher
 */
public final class Ja2
{
	public static void main(final String... args) throws InterruptedException, InvocationTargetException
    {
		Thread.currentThread().setName("User-main");
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			@SuppressWarnings("synthetic-access")
			public void run()
			{
		    	final Ja2 program = new Ja2();
		    	program.run(args);
			}
    	}
    	);
    }



    private boolean gui = true;
	private String config = "ja2.cfg";

	private Emulator emu;



	private Ja2()
    {
    	// only instantiated by main
    }

    public void run(final String... args)
	{
    	try
		{
			tryRun(args);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

    private void tryRun(final String... args) throws IOException, InvalidMemoryLoad, InvalidDiskImage
    {
    	parseArgs(args);

    	final Config cfg = new Config(this.config);

		this.emu = new Emulator();
		this.emu.config(cfg,new StandardIn.EOFHandler()
		{
			@SuppressWarnings("synthetic-access")
			public void handleEOF()
			{
				if (!Ja2.this.gui)
				{
					Ja2.this.emu.close();
				}
			}
		});

		if (this.gui)
		{
			this.emu.initGUI();
		}
    }

    private void parseArgs(final String... args)
	{
		for (final String arg : args)
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
