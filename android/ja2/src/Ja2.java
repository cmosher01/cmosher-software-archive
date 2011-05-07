import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import cards.disk.InvalidDiskImage;
import chipset.InvalidMemoryLoad;
import config.CmdLineArgs;
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
	private static String[] rarg;

	/**
	 * Runs the emulator.
	 * 
	 * @param args array of command line arguments
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void main(final String... args) throws InterruptedException, InvocationTargetException
    {
		Thread.currentThread().setName("User-main");

		Ja2.rarg = args;

		/*
		 * Start Swing's event dispatch thread right away, and
		 * run our program on it. The main thread will wait here
		 * until we are sure the event dispatch thread is
		 * running. The program's run method will set everything
		 * up (while running on the event dispatch thread), and
		 * then return. After that, invokeAndWait will return
		 * to us, and we exit the main thread.
		 */
    	SwingUtilities.invokeAndWait(new Ja2());
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
    	final CmdLineArgs cli = new CmdLineArgs(Ja2.rarg);

    	final Emulator emu = cli.isGUI() ? new GUIEmulator() : new CLIEmulator();

    	final Config cfg = new Config(cli.getConfig());
    	emu.config(cfg);

    	emu.init();
    }

}
