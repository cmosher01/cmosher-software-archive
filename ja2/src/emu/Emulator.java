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
import keyboard.HyperMode;
import keyboard.KeyboardBufferMode;
import keyboard.KeyboardProducer;
import keyboard.KeypressQueue;
import paddle.PaddleButtonStates;
import paddle.PaddleButtons;
import video.AnalogTV;
import video.DisplayType;
import video.ScreenImage;
import video.VideoDisplayDevice;
import video.VideoStaticGenerator;
import cards.disk.InvalidDiskImage;
import cards.stdio.StandardIn;
import chipset.InvalidMemoryLoad;
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
	private final HyperMode hyper;
	private final KeyboardBufferMode buffered;
	private final KeypressQueue keypresses;
	private final PaddleButtonStates paddleButtonStates;
	private final Apple2 apple2;
	private final VideoStaticGenerator videoStatic;
	private final ScreenImage screenImage;
	private final VideoDisplayDevice display;

	private TimingGenerator timer;

	private Screen screen;



	public Emulator() throws IOException
	{
		this.throttle = new Throttle();
		this.hyper = new HyperMode();
		this.buffered = new KeyboardBufferMode();

		this.screenImage = new ScreenImage();
    	this.display = new AnalogTV(this.screenImage);

    	this.keypresses = new KeypressQueue();

    	this.paddleButtonStates = new PaddleButtonStates();

    	this.apple2 = new Apple2(this.keypresses,this.paddleButtonStates,this.display,this.hyper,this.buffered);

		this.videoStatic = new VideoStaticGenerator(this.display);
	}

	public void initGUI()
	{
		this.screen = new Screen(this.screenImage);
    	final ComputerControlPanel compControls = new ComputerControlPanel(this);
    	final MonitorControlPanel monitorControls = new MonitorControlPanel(this);

    	new GUI(this,this.screen,compControls,monitorControls,this.apple2.slots);

    	this.screenImage.addObserver(new Observer()
		{
    		@SuppressWarnings({ "unused", "synthetic-access" })
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
	}

	public void initCLI()
	{
		// TODO fix CLI
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

		this.screen.addKeyListener(new KeyboardProducer(this.keypresses));
		this.screen.addKeyListener(new ClipboardProducer(this.keypresses));
		this.screen.addKeyListener(new HyperKeyHandler(this.hyper,this.buffered));
		this.screen.addKeyListener(new FnKeyHandler(this.apple2,this.screenImage,this.apple2.ram,this.throttle));
	    this.screen.addKeyListener(new PaddleButtons(this.paddleButtonStates));
	}

	public void powerOnComputer()
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
		// TODO ask if unsaved changes
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
		if (timer != null)
		{
			timer.shutdown();
			timer = null;
		}
	}

	public void config(final Config cfg, final StandardIn.EOFHandler eof) throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{
		cfg.parseConfig(this.apple2.rom,this.apple2.slots,this.hyper,eof);
	}
}
