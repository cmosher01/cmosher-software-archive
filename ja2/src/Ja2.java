import gui.GUI;
import gui.Screen;
import gui.UI;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import paddle.PaddleBtnInterface;
import paddle.PaddleButtons;
import paddle.Paddles;
import paddle.PaddlesInterface;
import keyboard.ClipboardProducer;
import keyboard.FnKeyHandler;
import keyboard.HyperKeyHandler;
import keyboard.Keyboard;
import keyboard.KeyboardInterface;
import keyboard.KeyboardProducer;
import keyboard.KeypressQueue;
import keyboard.NullKeyboard;
import stdio.StandardIn;
import stdio.StandardInProducer;
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
import cli.CLI;
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
	private Apple apple;



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

    private void tryRunTest(final String... args)
    {
    	parseArgs(args);

//    	final DiskBytes disk1 = new DiskBytes();
//    	final DiskBytes disk2 = new DiskBytes();
//    	final BufferedImage screenImage = new BufferedImage(Video.SIZE.width,Video.SIZE.height,BufferedImage.TYPE_INT_RGB);
//    	final Screen screen = new Screen();
//        final UI ui = new GUI(this,screen,disk1,disk2,diskState,screenImage);
//    	final KeypressQueue keypresses = new KeypressQueue(NOTIFY_ON_PUT);
//    	final KeypressQueue stdinkeys = new KeypressQueue();
//    	final PaddlesInterface paddles = new Paddles();
//    	final PaddleBtnInterface pdlbtns = new PaddleButtons();
//    	this.apple = new Apple(ui,screenImage,keypresses,stdinkeys,paddles,pdlbtns,new DiskBytes[] {disk1,disk2});
//		final KeyboardProducer keyprod = new KeyboardProducer(keypresses);
//    	screen.addKeyListener(keyprod);
//    	final ClipboardProducer clip = new ClipboardProducer(keypresses);
//    	screen.addKeyListener(clip);
//		final HyperKeyHandler hyper = new HyperKeyHandler(ui);
//    	screen.addKeyListener(hyper);
//    	final FnKeyHandler fn = new FnKeyHandler(this.apple);//cpu,video,memory);
//        screen.addKeyListener(fn);
//        screen.addKeyListener((PaddleButtons)pdlbtns);
//        screen.setFocusTraversalKeysEnabled(false);
//        screen.requestFocus();
//    	try
//		{
//    		Config cfg = new Config(this.config);
//			cfg.parseConfig(this.apple/*memory*/,disk1,disk2);
//		}
//		catch (final Exception e)
//		{
//			e.printStackTrace();
//			ui.showMessage(e.getMessage());
//		}
//        ui.updateDrives();
//
//        this.apple.start();
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

    	final KeypressQueue keypresses = new KeypressQueue(NOTIFY_ON_PUT);

    	final KeyboardInterface keyboard = new Keyboard(keypresses,ui);

        final Memory memory = new Memory();

    	final DiskInterface disk = new DiskInterface(diskState,ui);
        final Video video = new Video(ui,memory,screenImage);
    	final PaddlesInterface paddles = new Paddles();
    	final KeypressQueue stdinkeys = new KeypressQueue();
    	final StandardInProducer stdinprod = new StandardInProducer(stdinkeys);
    	final List<Card> cards = Arrays.<Card>asList(new Card[]
		{
	    	/* 0 */ new EmptySlot(),
	    	/* 1 */ new StandardOut(),
	    	/* 2 */ new StandardIn(ui,stdinkeys),
	    	/* 3 */ new EmptySlot(),
	    	/* 4 */ new EmptySlot(),
	    	/* 5 */ new EmptySlot(),
	    	/* 6 */ disk,
	    	/* 7 */ new EmptySlot()
		});
    	final Slots slots = new Slots(cards);
    	final PaddleButtons pdlbtns = new PaddleButtons();
        final AddressBus addressBus = new AddressBus(memory,keyboard,video,paddles,pdlbtns,slots);


    	final CPU6502 cpu = new CPU6502(addressBus);

    	this.clock = new Clock(cpu,video,drive,paddles,ui);

    	if (screen != null)
    	{
    		final KeyboardProducer keyprod = new KeyboardProducer(keypresses);
	    	screen.addKeyListener(keyprod);
	    	final ClipboardProducer clip = new ClipboardProducer(keypresses);
	    	screen.addKeyListener(clip);
    		final HyperKeyHandler hyper = new HyperKeyHandler(ui);
	    	screen.addKeyListener(hyper);
	    	final FnKeyHandler fn = new FnKeyHandler(cpu,screenImage,memory);
	        screen.addKeyListener(fn);
	        screen.addKeyListener(pdlbtns);
	        screen.setFocusTraversalKeysEnabled(false);
	        screen.requestFocus();
    	}



    	try
		{
    		final Config cfg = new Config(this.config);
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
