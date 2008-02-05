/*
 * Created on Feb 4, 2008
 */
package emu;


import java.io.IOException;
import config.Config;
import gui.ComputerControlPanel;
import gui.GUI;
import gui.MonitorControlPanel;
import gui.Screen;
import gui.UI;
import paddle.PaddleButtons;
import cli.CLI;
import keyboard.ClipboardProducer;
import keyboard.FnKeyHandler;
import keyboard.HyperKeyHandler;
import keyboard.KeyboardProducer;
import keyboard.KeypressQueue;
import keyboard.VideoKeyHandler;
import cards.stdio.StandardIn;
import chipset.Apple2;
import chipset.RAMInitializer;
import chipset.Throttle;
import chipset.TimingGenerator;
import video.AnalogTV;
import video.DisplayType;
import video.ScreenImage;
import video.VideoDisplayDevice;
import video.VideoStaticGenerator;

public class Emulator
{
	private final KeypressQueue keypresses = new KeypressQueue();
	private final Throttle throttle = new Throttle();

	private TimingGenerator timer;
	private Apple2 apple2;
	private VideoStaticGenerator videoStatic;
	private VideoDisplayDevice display;

	public Emulator(final boolean gui)
	{
		final ScreenImage screenImage = new ScreenImage();

        final UI ui;
    	if (gui)
    	{
    		final Screen screen = new Screen(screenImage);
	    	final ComputerControlPanel compControls = new ComputerControlPanel(this);
	    	final MonitorControlPanel monitorControls = new MonitorControlPanel(this);
	    	ui = new GUI(this,screen,compControls,monitorControls,slots);

	    	screen.addKeyListener(new KeyboardProducer(keypresses,keyboard));
	    	screen.addKeyListener(new ClipboardProducer(keypresses));
	    	screen.addKeyListener(new HyperKeyHandler(hyper));
	        screen.addKeyListener(new FnKeyHandler(cpu,screenImage,ram,this.throttle));
	        screen.addKeyListener(new VideoKeyHandler(video));
	        screen.addKeyListener(new PaddleButtons(paddleButtonStates));


	        screen.setFocusTraversalKeysEnabled(false);
	        screen.requestFocus();
    	}
    	else
    	{
        	ui = new CLI();
    	}

    	this.display = new AnalogTV(screenImage,ui);
	}

	public void powerOnComputer() throws IOException
	{
		this.apple2 = new Apple2(this.keypresses);
    	this.timer = new TimingGenerator(this.apple2,this.throttle);
	}

	public void powerOffComputer()
	{
    	this.videoStatic = new VideoStaticGenerator(this.display);
    	this.timer = new TimingGenerator(this.videoStatic,this.throttle);
	}

	public void powerOnMonitor()
	{
		this.apple2.setDisplay(this.display);
	}

	public void powerOffMonitor()
	{
		this.apple2.setDisplay(null);
	}

	public void setDisplayType(DisplayType type)
	{
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
				if (clock != null)
				{
					if (clock.isRunning())
						clock.shutdown();
				}
			}
		});
		th.setName("Ja2-close-shutdown clock");
		th.setDaemon(true);
		th.start();
	}

	public void config(final Config cfg)
	{
		cfg.parseConfig(rom,slots,hyper,new StandardIn.EOFHandler()
		{
			@SuppressWarnings("synthetic-access")
			public void handleEOF()
			{
				if (!gui)
				{
					close();
				}
			}
		});
	}
}
