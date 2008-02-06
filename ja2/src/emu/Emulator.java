/*
 * Created on Feb 4, 2008
 */
package emu;


import java.awt.event.KeyListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import keyboard.ClipboardProducer;
import keyboard.FnKeyHandler;
import keyboard.HyperKeyHandler;
import keyboard.KeyboardProducer;
import keyboard.KeypressQueue;
import keyboard.VideoKeyHandler;
import paddle.PaddleButtons;
import video.AnalogTV;
import video.DisplayType;
import video.ScreenImage;
import video.VideoDisplayDevice;
import video.VideoStaticGenerator;
import cards.disk.InvalidDiskImage;
import cards.stdio.StandardIn;
import chipset.InvalidMemoryLoad;
import chipset.Slots;
import chipset.Throttle;
import chipset.TimingGenerator;
import config.Config;
import gui.ComputerControlPanel;
import gui.GUI;
import gui.MonitorControlPanel;
import gui.Screen;

public class Emulator implements Closeable
{
	private final Throttle throttle;
	private final KeypressQueue keypresses;
	private final Apple2 apple2;
	private final VideoStaticGenerator videoStatic;
	private final ScreenImage screenImage;
	private final VideoDisplayDevice display;

	private TimingGenerator timer;

	private Screen screen;



	public Emulator() throws IOException
	{
		this.throttle = new Throttle();

		this.keypresses = new KeypressQueue();

		this.apple2 = new Apple2(this.keypresses);

		this.videoStatic = new VideoStaticGenerator();

		this.screenImage = new ScreenImage();
    	this.display = new AnalogTV(this.screenImage);
	}

	public void initGUI()
	{
		this.screen = new Screen(this.screenImage);
    	final ComputerControlPanel compControls = new ComputerControlPanel(this);
    	final MonitorControlPanel monitorControls = new MonitorControlPanel(this);

    	new GUI(this,screen,compControls,monitorControls,this.apple2.slots);

    	this.screenImage.addObserver(new Observer()
		{
    		@SuppressWarnings("unused")
			public void update(final Observable observableThatChagned, final Object typeOfChange)
			{
    			Emulator.this.screen.plot();
			}
		});

    	initKeyListeners();


    	this.screen.setFocusTraversalKeysEnabled(false);
    	this.screen.requestFocus();

    	setDisplayType(DisplayType.MONITOR_COLOR);
    	powerOffComputer();
    	powerOffMonitor();
		this.videoStatic.setDisplay(this.display);
		this.apple2.setDisplay(this.display);
	}

	private void initKeyListeners()
	{
		if (this.screen == null)
		{
			return;
		}
		final KeyListener[] rkl = this.screen.getKeyListeners();
		for (final KeyListener listener: rkl)
		{
			this.screen.removeKeyListener(listener);
		}

		this.screen.addKeyListener(new KeyboardProducer(this.keypresses,this.apple2.keyboard));
		this.screen.addKeyListener(new ClipboardProducer(this.keypresses));
		this.screen.addKeyListener(new HyperKeyHandler(this.apple2.hyper));
		this.screen.addKeyListener(new FnKeyHandler(this.apple2.cpu,this.screenImage,this.apple2.ram,this.throttle));
	    this.screen.addKeyListener(new VideoKeyHandler(this.apple2.video));
	    this.screen.addKeyListener(new PaddleButtons(this.apple2.paddleButtonStates));
	}

	public void powerOnComputer() throws IOException
	{
		if (this.timer != null)
		{
			this.timer.shutdown();
			this.timer = null;
		}
    	this.display.restartSignal();
		this.apple2.powerOn();
		initKeyListeners();
    	this.timer = new TimingGenerator(this.apple2,this.throttle);
    	this.timer.run();
	}

	public void powerOffComputer()
	{
		if (this.timer != null)
		{
			this.timer.shutdown();
			this.timer = null;
		}
		this.apple2.powerOff();
    	this.timer = new TimingGenerator(this.videoStatic,this.throttle);
    	this.timer.run();
	}

	public void powerOnMonitor()
	{
		this.display.powerOn(true);
	}

	public void powerOffMonitor()
	{
		this.display.powerOn(false);
	}

	public void setDisplayType(DisplayType type)
	{
		this.display.setType(type);
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
				if (timer != null)
				{
					timer.shutdown();
					timer = null;
				}
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

	public void config(final Config cfg, final StandardIn.EOFHandler eof) throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{
		cfg.parseConfig(this.apple2.rom,this.apple2.slots,this.apple2.hyper,eof);
	}
}
