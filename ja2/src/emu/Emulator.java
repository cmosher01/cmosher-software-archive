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
	private final KeypressQueue keypresses;
	private final Throttle throttle;
	private TimingGenerator timer;
	private final Slots slots = new Slots();
	private Apple2 apple2;
	private VideoStaticGenerator videoStatic;
	private final ScreenImage screenImage;
	private VideoDisplayDevice display;

	private Screen screen;



	public Emulator() throws IOException
	{
		this.keypresses = new KeypressQueue();
		this.throttle = new Throttle();
		this.apple2 = new Apple2(this.keypresses,this.slots);
		this.screenImage = new ScreenImage();
    	this.display = new AnalogTV(screenImage);
	}

	public void initGUI()
	{
		this.screen = new Screen(screenImage);
    	final ComputerControlPanel compControls = new ComputerControlPanel(this);
    	final MonitorControlPanel monitorControls = new MonitorControlPanel(this);

    	new GUI(this,screen,compControls,monitorControls,this.slots);

    	screenImage.addObserver(new Observer()
		{
    		@SuppressWarnings("unused")
			public void update(final Observable observableThatChagned, final Object typeOfChange)
			{
				screen.plot();
			}
		});

    	initKeyListeners();


        screen.setFocusTraversalKeysEnabled(false);
        screen.requestFocus();
	}

	private void initKeyListeners()
	{
		final KeyListener[] rkl = this.screen.getKeyListeners();
		for (final KeyListener listener: rkl)
		{
			this.screen.removeKeyListener(listener);
		}

		this.screen.addKeyListener(new KeyboardProducer(keypresses,this.apple2.keyboard));
		this.screen.addKeyListener(new ClipboardProducer(keypresses));
		this.screen.addKeyListener(new HyperKeyHandler(this.apple2.hyper));
		this.screen.addKeyListener(new FnKeyHandler(this.apple2.cpu,screenImage,this.apple2.ram,this.throttle));
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
		this.apple2 = new Apple2(this.keypresses,this.slots);
		initKeyListeners();
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

	public void config(final Config cfg, final StandardIn.EOFHandler eof) throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{
		cfg.parseConfig(this.apple2.rom,this.apple2.slots,this.apple2.hyper,eof);
	}
}
