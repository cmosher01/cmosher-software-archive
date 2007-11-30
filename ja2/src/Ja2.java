import gui.FrameManager;
import gui.Screen;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import keyboard.ClipboardHandler;
import keyboard.FnKeyHandler;
import keyboard.Keyboard;
import keyboard.Paddles;
import util.Util;
import video.Video;
import chipset.AddressBus;
import chipset.CPU6502;
import chipset.Clock;
import chipset.InvalidMemoryLoad;
import chipset.Memory;
import disk.DiskBytes;
import disk.DiskDriveSimple;
import disk.DiskInterface;
import disk.StepperMotor;

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
public final class Ja2
{
    public static void main(final String... args) throws InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			public void run()
			{
		    	final Ja2 program = new Ja2();
		    	program.run(args);
			}
    	}
    	);
    }



    FrameManager framer;
	Clock clock;

	private String config = "ja2.cfg";



	Ja2()
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

    private void tryRun(final String... args)
    {
    	parseArgs(args);




    	final DiskBytes disk1 = new DiskBytes();
    	final DiskBytes disk2 = new DiskBytes();
    	final DiskDriveSimple drive = new DiskDriveSimple(new DiskBytes[] {disk1,disk2});
    	final StepperMotor arm = new StepperMotor();
    	final DiskInterface disk = new DiskInterface(drive,arm,this.framer);


    	final Screen screen = new Screen();

    	final Keyboard keyboard = new Keyboard();
    	final ClipboardHandler clip = new ClipboardHandler(keyboard);
        final Memory memory = new Memory();
    	final Video video = new Video(this.framer,memory);

        this.framer = new FrameManager(
	    	new WindowAdapter()
	    	{
				@Override
				public void windowClosing(@SuppressWarnings("unused") final WindowEvent e)
				{
					close();
				}
	    	},
	    	screen,disk1,disk2,disk,video.getImage());

    	final Paddles paddles = new Paddles(4,screen.getTopLevelAncestor());
        final AddressBus addressBus = new AddressBus(memory,keyboard,video,paddles,disk);


    	final CPU6502 cpu = new CPU6502(addressBus);

    	final FnKeyHandler fn = new FnKeyHandler(cpu,disk,clip,video,memory);

    	screen.addKeyListener(keyboard);
        screen.addKeyListener(fn);
        screen.setFocusTraversalKeysEnabled(false);
        screen.requestFocus();

    	this.clock = new Clock(cpu,video,drive,paddles,keyboard);



    	try
		{
			parseConfig(memory);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			this.framer.showMessage(e.getMessage());
		}



    	this.clock.run();
        this.framer.updateDrives();
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
		else
		{
			throw new IllegalArgumentException(arg);
		}
	}

	private static final Pattern patIMPORT = Pattern.compile("import\\s+(.+)\\s+(.+)");
	private void parseConfig(Memory memory) throws IOException, InvalidMemoryLoad
	{
    	final BufferedReader cfg = new BufferedReader(new InputStreamReader(new FileInputStream(this.config)));
    	for (String s = cfg.readLine(); s != null; s = cfg.readLine())
    	{
    		int comment = s.indexOf('#');
    		if (comment >= 0)
    		{
    			s = s.substring(0,comment);
    		}
    		s = s.trim();
    		if (s.isEmpty())
    		{
    			continue;
    		}

    		final Matcher matcher;
    		if ((matcher = patIMPORT.matcher(s)).matches())
    		{
    			final int addr = Integer.decode(matcher.group(1));
    			final String mem = matcher.group(2);

    			final InputStream image = new FileInputStream(new File(mem));
    	        memory.load(addr,image);
    	        image.close();
    		}
    		else
    		{
    			throw new IllegalArgumentException("Error in config file: "+s);
    		}
    	}
    	cfg.close();
	}

	public void close()
	{
		// TODO check for unsaved changes to disks before exiting application

		// use another thread (a daemon one) to avoid any deadlocks
		// (for example, if this method is called on the dispatch thread)
		final Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				Ja2.this.framer.close(); // this exits the app
				if (Ja2.this.clock != null)
				{
					Ja2.this.clock.shutdown();
				}
			}
		});
		th.setDaemon(true);
		th.start();
	}
}
