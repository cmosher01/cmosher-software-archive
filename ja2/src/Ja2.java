import gui.GUI;
import gui.Screen;
import gui.UI;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import config.Config;
import keyboard.ClipboardProducer;
import keyboard.FnKeyHandler;
import keyboard.HyperKeyHandler;
import keyboard.Keyboard;
import keyboard.KeyboardInterface;
import keyboard.KeyboardProducer;
import keyboard.KeypressQueue;
import keyboard.VideoKeyHandler;
import paddle.PaddleButtons;
import paddle.Paddles;
import paddle.PaddlesInterface;
import util.Util;
import video.Video;
import chipset.AddressBus;
import chipset.Card;
import chipset.Clock;
import chipset.Memory;
import chipset.Slots;
import chipset.Throttle;
import chipset.cpu.CPU6502;
import cli.CLI;
import disk.DiskBytes;
import disk.DiskDriveSimple;
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
    private static final boolean NOTIFY_ON_PUT = true;



	public static void main(final String... args) throws InterruptedException, InvocationTargetException
    {
		Thread.currentThread().setName("Ja2-main");
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





    	final BufferedImage screenImage = new BufferedImage(Video.SIZE.width,Video.SIZE.height,BufferedImage.TYPE_INT_RGB);

    	final Slots slots = new Slots();

    	final Screen screen;
        final UI ui;
    	if (this.gui)
    	{
	    	screen = new Screen();
	    	ui = new GUI(this,screen,cards,screenImage);
    	}
    	else
    	{
    		screen = null;
        	ui = new CLI(this);
    	}



//    	RAM @ $0000 thru $BFFF
    	final Memory ram = new Memory(0xC000);
//    	ROM @ $D000 thru $FFFF
    	final Memory rom = new Memory(0x10000-0xD000);



    	try
		{
    		final Config cfg = new Config(this.config);
			cfg.parseConfig(rom,cards,ui);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			ui.showMessage(e.getMessage());
		}



		final KeypressQueue keypresses = new KeypressQueue(NOTIFY_ON_PUT);

    	final KeyboardInterface keyboard = new Keyboard(keypresses,ui);

        final Video video = new Video(ui,ram,screenImage);
    	final PaddlesInterface paddles = new Paddles();

    	final PaddleButtons pdlbtns = new PaddleButtons();



    	final AddressBus addressBus = new AddressBus(ram,rom,keyboard,video,paddles,pdlbtns,slots);



    	final CPU6502 cpu = new CPU6502(addressBus);



    	final Throttle throttle = new Throttle(video,cards,ui);

    	this.clock = new Clock(cpu,video,paddles,throttle);



    	if (screen != null)
    	{
    		final KeyboardProducer keyprod = new KeyboardProducer(keypresses);
	    	screen.addKeyListener(keyprod);
	    	final ClipboardProducer clip = new ClipboardProducer(keypresses);
	    	screen.addKeyListener(clip);
    		final HyperKeyHandler hyper = new HyperKeyHandler(ui);
	    	screen.addKeyListener(hyper);
	    	final FnKeyHandler fn = new FnKeyHandler(cpu,screenImage,ram);
	        screen.addKeyListener(fn);
	        final VideoKeyHandler vid = new VideoKeyHandler(video);
	        screen.addKeyListener(vid);
	        screen.addKeyListener(pdlbtns);
	        screen.setFocusTraversalKeysEnabled(false);
	        screen.requestFocus();
    	}






    	this.clock.run();
        ui.updateDrives(true);
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
		th.setName("Ja2-close-shutdown clock");
		th.setDaemon(true);
		th.start();
	}
}
