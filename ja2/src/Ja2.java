import gui.GUI;
import gui.Screen;
import gui.UI;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import keyboard.ClipboardProducer;
import keyboard.FnKeyHandler;
import keyboard.HyperKeyHandler;
import keyboard.Keyboard;
import keyboard.KeyboardInterface;
import keyboard.KeyboardProducer;
import keyboard.KeypressQueue;
import keyboard.VideoKeyHandler;
import paddle.PaddleButtonStates;
import paddle.PaddleButtons;
import paddle.Paddles;
import paddle.PaddlesInterface;
import speaker.SpeakerClicker;
import util.Util;
import video.AnalogTV;
import video.PictureGenerator;
import video.SimpleScreenImage;
import video.TelevisionScreenImage;
import video.Video;
import video.VideoMode;
import cards.disk.InvalidDiskImage;
import cards.stdio.StandardIn;
import chipset.AddressBus;
import chipset.TimingGenerator;
import chipset.InvalidMemoryLoad;
import chipset.Memory;
import chipset.Slots;
import chipset.Throttle;
import chipset.cpu.CPU6502;
import cli.CLI;
import config.Config;

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
		Thread.currentThread().setName("User-main");
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



    private boolean gui = true;
	private TimingGenerator clock;

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

    private void tryRun(final String... args) throws IOException, InvalidMemoryLoad, InvalidDiskImage
    {
    	parseArgs(args);





//    	RAM @ $0000 thru $BFFF
    	final Memory ram = new Memory(0xC000);
//    	ROM @ $D000 thru $FFFF
    	final Memory rom = new Memory(0x10000-0xD000);

    	final Slots slots = new Slots();

    	final Throttle throttle = new Throttle();



    	final Config cfg = new Config(this.config,throttle);
		cfg.parseConfig(rom,slots,new StandardIn.EOFHandler()
		{
			@SuppressWarnings("synthetic-access")
			public void handleEOF()
			{
				if (!Ja2.this.gui)
				{
					close();
				}
			}
		});



		final TelevisionScreenImage screenImage = new TelevisionScreenImage();

		final Screen screen;
        final UI ui;
    	if (this.gui)
    	{
	    	screen = new Screen(screenImage);
	    	ui = new GUI(this,screen,slots);
    	}
    	else
    	{
    		screen = null;
        	ui = new CLI();
    	}








    	
    	final VideoMode videoMode = new VideoMode();

		final KeypressQueue keypresses = new KeypressQueue();

    	final KeyboardInterface keyboard = new Keyboard(keypresses,throttle);

    	final PaddlesInterface paddles = new Paddles();

    	final PaddleButtonStates paddleButtonStates = new PaddleButtonStates();

    	final SpeakerClicker speaker = new SpeakerClicker();



    	final AddressBus addressBus = new AddressBus(ram,rom,keyboard,videoMode,paddles,paddleButtonStates,speaker,slots);



    	final AnalogTV tv = new AnalogTV();
    	final PictureGenerator picgen = new PictureGenerator(tv,videoMode,screenImage);
    	final Video video = new Video(videoMode,ui,addressBus,screenImage,picgen);



    	final CPU6502 cpu = new CPU6502(addressBus);



    	this.clock = new TimingGenerator(cpu,video,paddles,speaker,throttle);



    	if (screen != null)
    	{
	        screen.addKeyListener(new TestKeyHandler(tv));
	    	screen.addKeyListener(new KeyboardProducer(keypresses,keyboard));
	    	screen.addKeyListener(new ClipboardProducer(keypresses));
	    	screen.addKeyListener(new HyperKeyHandler(throttle));
	        screen.addKeyListener(new FnKeyHandler(cpu,screenImage,ram,throttle));
	        screen.addKeyListener(new VideoKeyHandler(video));
	        screen.addKeyListener(new PaddleButtons(paddleButtonStates));


	        screen.setFocusTraversalKeysEnabled(false);
	        screen.requestFocus();
    	}






    	this.clock.run();
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
			@SuppressWarnings("synthetic-access")
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
