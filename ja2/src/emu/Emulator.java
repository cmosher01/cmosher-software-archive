/*
 * Created on Feb 4, 2008
 */
package emu;


import java.io.Closeable;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
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
import cards.disk.InvalidDiskImage;
import cards.stdio.StandardIn;
import chipset.InvalidMemoryLoad;
import chipset.RAMInitializer;
import chipset.Throttle;
import chipset.TimingGenerator;
import video.AnalogTV;
import video.DisplayType;
import video.ScreenImage;
import video.VideoDisplayDevice;
import video.VideoStaticGenerator;

public class Emulator implements Closeable
{
	private final KeypressQueue keypresses = new KeypressQueue();
	private final Throttle throttle = new Throttle();

	private TimingGenerator timer;
	private Apple2 apple2;
	private VideoStaticGenerator videoStatic;
	private VideoDisplayDevice display;

	private final boolean gui;

	public Emulator(final boolean gui) throws IOException
	{
		this.gui = gui;

		this.apple2 = new Apple2(this.keypresses);
		final ScreenImage screenImage = new ScreenImage();
    	this.display = new AnalogTV(screenImage);

        final UI ui;
    	if (gui)
    	{
    		final Screen screen = new Screen(screenImage);
	    	final ComputerControlPanel compControls = new ComputerControlPanel(this);
	    	final MonitorControlPanel monitorControls = new MonitorControlPanel(this);
	    	ui = new GUI(this,screen,compControls,monitorControls,this.apple2.slots);

	    	screenImage.addObserver(new Observer()
			{
				public void update(final Observable observableThatChagned, final Object typeOfChange)
				{
					screen.plot();
				}
			});

	    	screen.addKeyListener(new KeyboardProducer(keypresses,this.apple2.keyboard));
	    	screen.addKeyListener(new ClipboardProducer(keypresses));
	    	screen.addKeyListener(new HyperKeyHandler(this.apple2.hyper));
	        screen.addKeyListener(new FnKeyHandler(this.apple2.cpu,screenImage,this.apple2.ram,this.throttle));
	        screen.addKeyListener(new VideoKeyHandler(this.apple2.video));
	        screen.addKeyListener(new PaddleButtons(this.apple2.paddleButtonStates));


	        screen.setFocusTraversalKeysEnabled(false);
	        screen.requestFocus();
    	}
    	else
    	{
        	ui = new CLI();
    	}
	}

	public void powerOnComputer() throws IOException
	{
		if (this.timer != null)
		{
			this.timer.shutdown();
			this.timer = null;
		}
		this.apple2 = new Apple2(this.keypresses);
    	this.timer = new TimingGenerator(this.apple2,this.throttle);
	}

	public void powerOffComputer()
	{
		if (this.timer != null)
		{
			this.timer.shutdown();
			this.timer = null;
		}
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
//				if (clock != null)
//				{
//					if (clock.isRunning())
//						clock.shutdown();
//				}
			}
		});
		th.setName("Ja2-close-shutdown clock");
		th.setDaemon(true);
		th.start();
	}

	public void config(final Config cfg) throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{
		cfg.parseConfig(this.apple2.rom,this.apple2.slots,this.apple2.hyper,new StandardIn.EOFHandler()
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
