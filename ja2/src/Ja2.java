import gui.GUI;
import gui.Screen;
import gui.UI;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import cli.CLI;
import keyboard.ClipboardHandler;
import keyboard.FnKeyHandler;
import keyboard.Keyboard;
import keyboard.KeyboardInterface;
import keyboard.NullKeyboard;
import keyboard.Paddles;
import stdio.StandardIn;
import stdio.StandardOut;
import util.Util;
import video.Video;
import chipset.AddressBus;
import chipset.Card;
import chipset.Clock;
import chipset.EmptySlot;
import chipset.Memory;
import chipset.Slots;
import chipset.cpu.CPU6502;
import disk.DiskBytes;
import disk.DiskDriveSimple;
import disk.DiskInterface;
import disk.DiskState;
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
public final class Ja2 implements Closeable
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



    boolean gui = true;
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

    	final Screen screen;
        final UI ui;
    	if (this.gui)
    	{
	    	screen = new Screen();
	    	ui = new GUI(this,screen,disk1,disk2,diskState,screenImage);
    	}
    	else
    	{
    		screen = null;
        	ui = new CLI(this);
    	}

        final Memory memory = new Memory();

    	final DiskInterface disk = new DiskInterface(diskState,ui);
        final Video video = new Video(ui,memory,screenImage);
    	final Paddles paddles = new Paddles();
    	final List<Card> cards = Arrays.<Card>asList(new Card[]
		{
	    	/* 0 */ new EmptySlot(),
	    	/* 1 */ new StandardOut(),
	    	/* 2 */ new StandardIn(ui),
	    	/* 3 */ new EmptySlot(),
	    	/* 4 */ new EmptySlot(),
	    	/* 5 */ new EmptySlot(),
	    	/* 6 */ disk,
	    	/* 7 */ new EmptySlot()
		});
    	final Slots slots = new Slots(cards);
    	final KeyboardInterface keyboard;
    	if (this.gui)
    	{
    		keyboard = new Keyboard();
    	}
    	else
    	{
    		keyboard = new NullKeyboard();
    	}
        final AddressBus addressBus = new AddressBus(memory,keyboard,video,paddles,slots);


    	final CPU6502 cpu = new CPU6502(addressBus);

    	if (this.gui)
    	{
	    	final ClipboardHandler clip = new ClipboardHandler((Keyboard)keyboard);
	    	final FnKeyHandler fn = new FnKeyHandler(cpu,disk,clip,video,memory);
	
	    	screen.addKeyListener((Keyboard)keyboard);
	        screen.addKeyListener(fn);
	        screen.setFocusTraversalKeysEnabled(false);
	        screen.requestFocus();
    	}

    	this.clock = new Clock(cpu,video,drive,paddles,keyboard);



    	try
		{
    		Config cfg = new Config(this.config);
			cfg.parseConfig(memory,disk1,disk2);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			ui.showMessage(e.getMessage());
		}



    	this.clock.run();
        ui.updateDrives();
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


	public void close()
	{
		// TODO check for unsaved changes to disks before exiting application (only in GUI mode)

		// use another thread (a daemon one) to avoid any deadlocks
		// (for example, if this method is called on the dispatch thread)
		final Thread th = new Thread(new Runnable()
		{
			public void run()
			{
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
