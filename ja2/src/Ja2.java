import gui.FrameManager;
import gui.Screen;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import keyboard.ClipboardHandler;
import keyboard.FnKeyHandler;
import keyboard.Keyboard;
import keyboard.Paddles;
import stdio.StandardIn;
import stdio.StandardOut;
import util.Util;
import video.Video;
import chipset.AddressBus;
import chipset.Card;
import chipset.Clock;
import chipset.EmptySlot;
import chipset.InvalidMemoryLoad;
import chipset.Memory;
import chipset.Slots;
import chipset.cpu.CPU6502;
import disk.DiskBytes;
import disk.DiskDriveSimple;
import disk.DiskInterface;
import disk.DiskState;
import disk.InvalidDiskImage;
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
    	final DiskState diskState = new DiskState(drive,arm);


    	final BufferedImage screenImage = new BufferedImage(Video.SIZE.width,Video.SIZE.height,BufferedImage.TYPE_INT_RGB);

    	final Screen screen = new Screen();

    	this.framer = new FrameManager(
	    	new WindowAdapter()
	    	{
				@Override
				public void windowClosing(@SuppressWarnings("unused") final WindowEvent e)
				{
					close();
				}
	    	},
	    	screen,disk1,disk2,diskState,screenImage);

    	final Keyboard keyboard = new Keyboard();
    	final ClipboardHandler clip = new ClipboardHandler(keyboard);
        final Memory memory = new Memory();

    	final DiskInterface disk = new DiskInterface(diskState,this.framer);
        final Video video = new Video(this.framer,memory,screenImage);
    	final Paddles paddles = new Paddles();
    	final List<Card> cards = Arrays.<Card>asList(new Card[]
		{
	    	/* 0 */ new EmptySlot(),
	    	/* 1 */ new StandardOut(),
	    	/* 2 */ new StandardIn(),
	    	/* 3 */ new EmptySlot(),
	    	/* 4 */ new EmptySlot(),
	    	/* 5 */ new EmptySlot(),
	    	/* 6 */ disk,
	    	/* 7 */ new EmptySlot()
		});
    	final Slots slots = new Slots(cards);
        final AddressBus addressBus = new AddressBus(memory,keyboard,video,paddles,slots);


    	final CPU6502 cpu = new CPU6502(addressBus);

    	final FnKeyHandler fn = new FnKeyHandler(cpu,disk,clip,video,memory);

    	screen.addKeyListener(keyboard);
        screen.addKeyListener(fn);
        screen.setFocusTraversalKeysEnabled(false);
        screen.requestFocus();

    	this.clock = new Clock(cpu,video,drive,paddles,keyboard);



    	try
		{
			parseConfig(memory,disk1,disk2);
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

	private static final Pattern patIMPORT = Pattern.compile("import\\s+(.+?)\\s+(.+?)");
	private static final Pattern patLOAD = Pattern.compile("load\\s+(.+?)\\s+(.+?)");
	private void parseConfig(final Memory memory, final DiskBytes disk1, final DiskBytes disk2) throws IOException, InvalidMemoryLoad, InvalidDiskImage
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

    		Matcher matcher;
    		if ((matcher = patIMPORT.matcher(s)).matches())
    		{
    			final int addr = Integer.decode(matcher.group(1));
    			final String mem = matcher.group(2);

    			final InputStream image = new FileInputStream(new File(mem));
    	        memory.load(addr,image);
    	        image.close();
    		}
    		else if ((matcher = patLOAD.matcher(s)).matches())
    		{
    			final int drive = Integer.decode(matcher.group(1));

    			final String nib = matcher.group(2);
    			final File fnib = new File(nib);

    			if (drive == 1)
    			{
    				disk1.load(fnib);
    			}
    			else if (drive == 2)
    			{
    				disk2.load(fnib);
    			}
    			else
    			{
        			throw new IllegalArgumentException("Error in config file: "+s);
    			}
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
				if (Ja2.this.framer != null)
				{
					Ja2.this.framer.close(); // this exits the app
				}
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
