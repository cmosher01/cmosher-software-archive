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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import cards.disk.InvalidDiskImage;
import cards.stdio.StandardIn;
import chipset.InvalidMemoryLoad;
import chipset.Throttle;
import chipset.TimingGenerator;
import config.CmdLineArgs;
import config.Config;
import display.AnalogTV;

public class Emulator extends Activity implements Closeable {
	private static final String LOG_TAG = Emulator.class.getName();
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

		this.screenImage.addObserver(new Observer() {
			private Screen screen;

			@Override
			@SuppressWarnings({ "unused" })
			public void update(final Observable observableThatChagned, final Object typeOfChange) {
				// Log.d(LOG_TAG, "plotting screen");
				if (this.screen == null) {
					this.screen = ((Screen) findViewById(R.id.screen));
				}
				this.screen.plot();
			}
		});
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.i(LOG_TAG, "display: "+metrics.heightPixels+"px X "+metrics.widthPixels+"px");

		// no cmd line args on android
		final CmdLineArgs cli = new CmdLineArgs(new String[0]);
		final Config cfg = new Config(getAssets(),cli.getConfig());
		try {
			config(cfg);
		} catch (final Throwable e) {
			Log.e(LOG_TAG, "Error trying to configure the emulator", e);
			finish(); // TODO
			return;
		}

		setContentView(R.layout.layout);

		final ToggleButton computerPowerToggleSwitch = (ToggleButton) findViewById(R.id.computerPower);
		computerPowerToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(@SuppressWarnings("unused") CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					powerOnComputer();
				} else {
					powerOffComputer();
				}
			}
		});
		final Button quit = (Button) findViewById(R.id.quit);
		quit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish(); // TODO
			}
		});
		final Button reset = (Button) findViewById(R.id.reset);
		reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Emulator.this.apple2.reset();
			}
		});

		this.display.powerOn(true);
		powerOffComputer();
	}

	@Override
	public void onPause() {
		super.onPause();
		// close(); // TODO
	}

	@Override
	public void onStop() {
		super.onStop();
		close(); // TODO
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		close(); // TODO
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

	@Override
	public void close() {
		if (this.timer != null) {
			this.timer.shutdown();
			this.timer = null;
		}
	}

	public void config(final Config cfg) throws IOException, InvalidMemoryLoad, InvalidDiskImage {
		cfg.parseConfig(this.apple2.rom, this.apple2.slots, this.hyper, new StandardIn.EOFHandler() {
			public void handleEOF() {
				// For a GUI, we don't do anything special when STDIN hits EOF
			}
		});
	}

	public ScreenImage getScreenImage() {
		return this.screenImage;
	}

}
