/*
 * Created on Feb 4, 2008
 */
package emu;

import gui.Screen;

import java.io.Closeable;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import keyboard.HyperMode;
import keyboard.KeyboardBufferMode;
import keyboard.KeypressQueue;
import nu.mine.mosher.android.epple2.R;
import paddle.PaddleButtonStates;
import video.ScreenImage;
import video.VideoDisplayDevice;
import video.VideoStaticGenerator;
import android.app.Activity;
import android.os.Bundle;
import chipset.Throttle;
import chipset.TimingGenerator;
import config.Config;
import display.AnalogTV;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class Emulator extends Activity implements Closeable {
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
	private Screen screen;

	public Emulator() throws IOException {
		this.throttle = new Throttle();
		this.hyper = new HyperMode();
		this.buffered = new KeyboardBufferMode();

		this.screenImage = new ScreenImage();
		this.display = new AnalogTV(this.screenImage);

		this.keypresses = new KeypressQueue();

		this.paddleButtonStates = new PaddleButtonStates();

		this.apple2 = new Apple2(this.keypresses, this.paddleButtonStates, this.display, this.hyper, this.buffered);

		this.videoStatic = new VideoStaticGenerator(this.display);

		this.screen = new Screen(this,this.screenImage);

		this.screenImage.addObserver(new Observer() {
			@SuppressWarnings({ "unused", "synthetic-access" })
			public void update(final Observable observableThatChagned, final Object typeOfChange) {
				Emulator.this.screen.plot();
			}
		});
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout);

		final ToggleButton computerPowerToggleSwitch = (ToggleButton) findViewById(R.id.computerPower);
		computerPowerToggleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(@SuppressWarnings("unused") CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					powerOnComputer();
				} else {
					powerOffComputer();
				}
			}
		});

		final ToggleButton monitorPowerToggleSwitch = (ToggleButton) findViewById(R.id.monitorPower);
		monitorPowerToggleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(@SuppressWarnings("unused") CompoundButton buttonView, final boolean isChecked) {
				Emulator.this.display.powerOn(isChecked);
			}
		});

		this.display.powerOn(false);
		powerOffComputer();
	}

	public void powerOnComputer() {
		if (this.timer != null) {
			this.timer.shutdown();
			this.timer = null;
		}
		this.apple2.powerOn();

		this.timer = new TimingGenerator(this.apple2, this.throttle);
		this.timer.run();
	}

	public void powerOffComputer() {
		if (this.timer != null) {
			this.timer.shutdown();
			this.timer = null;
		}
		// TODO ask if unsaved changes
		this.apple2.powerOff();
		this.timer = new TimingGenerator(this.videoStatic, this.throttle);
		this.timer.run();
	}

	public void close() {
		if (this.timer != null) {
			this.timer.shutdown();
			this.timer = null;
		}
	}

	// protected abstract StandardIn.EOFHandler getStdInEOF();

	public void config(final Config cfg) // throws IOException, InvalidMemoryLoad,
																				// InvalidDiskImage
	{
		// cfg.parseConfig(this.apple2.rom,this.apple2.slots,this.hyper,getStdInEOF());
	}

}
