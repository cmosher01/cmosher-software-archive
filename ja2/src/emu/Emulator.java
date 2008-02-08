/*
 * Created on Feb 4, 2008
 */
package emu;

import java.io.Closeable;
import java.io.IOException;
import keyboard.HyperMode;
import keyboard.KeyboardBufferMode;
import keyboard.KeypressQueue;
import paddle.PaddleButtonStates;
import video.ScreenImage;
import video.VideoDisplayDevice;
import video.VideoStaticGenerator;
import cards.disk.InvalidDiskImage;
import cards.stdio.StandardIn;
import chipset.InvalidMemoryLoad;
import chipset.Throttle;
import chipset.TimingGenerator;
import config.Config;
import display.AnalogTV;

public abstract class Emulator implements Closeable
{
	protected final Throttle throttle;
	protected final HyperMode hyper;
	protected final KeyboardBufferMode buffered;
	protected final KeypressQueue keypresses;
	protected final PaddleButtonStates paddleButtonStates;
	protected final Apple2 apple2;
	private final VideoStaticGenerator videoStatic;
	protected final ScreenImage screenImage;
	protected final VideoDisplayDevice display;

	private TimingGenerator timer;



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

	public abstract void init();

	public void powerOnComputer()
	{
		if (this.timer != null)
		{
			this.timer.shutdown();
			this.timer = null;
		}
    	this.display.restartSignal();
		this.apple2.powerOn();

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

	public void close()
	{
		if (this.timer != null)
		{
			this.timer.shutdown();
			this.timer = null;
		}
	}

	protected abstract StandardIn.EOFHandler getStdInEOF();

	public void config(final Config cfg) throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{
		cfg.parseConfig(this.apple2.rom,this.apple2.slots,this.hyper,getStdInEOF());
	}
}
