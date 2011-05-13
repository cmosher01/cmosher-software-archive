/*
 * Created on Feb 4, 2008
 */
package emu;

import gui.Screen;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import android.provider.Settings.System;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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

	private boolean shift;
	private boolean control;

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
		final CmdLineArgs noCmdLineArgs = new CmdLineArgs(new String[0]);
		final Config cfg = new Config(getAssets(),noCmdLineArgs.getConfig());
		try {
			config(cfg);
		} catch (final Throwable e) {
			Log.e(LOG_TAG, "Error trying to configure the emulator", e);
			finish(); // TODO
			return;
		}

		try {
			File filesDir = getFilesDir().getCanonicalFile().getAbsoluteFile();
			Log.i(LOG_TAG,"getFilesDir() --> "+filesDir.getPath());
		} catch (IOException e) {
			e.printStackTrace();
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

		final ToggleButton lShift = (ToggleButton) findViewById(R.id.lshift);
		lShift.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Emulator.this.shift = ((CompoundButton)v).isChecked(); // TODO fix interaction between two shift buttons
			}
		});
		final ToggleButton rShift = (ToggleButton) findViewById(R.id.rshift);
		rShift.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Emulator.this.shift = ((CompoundButton)v).isChecked();
			}
		});
		final ToggleButton ctrl = (ToggleButton) findViewById(R.id.ctrl);
		ctrl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Emulator.this.control = ((CompoundButton)v).isChecked();
			}
		});

//		final ToggleButton kbd = (ToggleButton) findViewById(R.id.keyboard);
//		kbd.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				final boolean compress = ((CompoundButton)v).isChecked();
//				Emulator.this.display.setCompress(compress);
//				Emulator.this.screenImage.setCompress(compress);
//				View screenView = findViewById(R.id.screen);
//				ViewGroup.LayoutParams params = screenView.getLayoutParams();
//				params.height = compress?192:192*2;
//				screenView.setLayoutParams(params);
//			}
//		});

		final Button paste = (Button) findViewById(R.id.paste);
		paste.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ClipboardManager clip = (ClipboardManager)Emulator.this.getSystemService(CLIPBOARD_SERVICE);
				final CharSequence sq = clip.getText();
				for (int i = 0; i < sq.length(); ++i) {
					char c = sq.charAt(i);
					Log.i(LOG_TAG,"paste: "+Integer.toHexString(c));
					if (c == '\n')
					{
						c = '\r';
					}
					Emulator.this.keypresses.put(c);
				}
			}
		});
		
		this.display.powerOn(true);
		powerOffComputer();
		Emulator.this.display.setCompress(true);
		Emulator.this.screenImage.setCompress(true);
	}

	private static class KeyCodes {
		public final int alone;
		public final int control;
		public final int shift;
		public final int both;
		public KeyCodes(final int alone, final int control, final int shift, final int both) {
			this.alone = alone;
			this.control = control;
			this.shift = shift;
			this.both = both;
		}
		public int get(final boolean isShift, final boolean isControl) {
			if (isShift && isControl) {
				return this.both;
			}
			if (isShift) {
				return this.shift;
			}
			if (isControl) {
				return this.control;
			}
			return this.alone;
		}
	}
	private static final Map<String,KeyCodes> mpKeys = new HashMap<String,KeyCodes>(45,1.0f);

	static {
		mpKeys.put("space",new KeyCodes(0xA0,0xA0,0xA0,0xA0));
		mpKeys.put("comma",new KeyCodes(0xAC,0xAC,0xBC,0xBC));
		mpKeys.put("dash",new KeyCodes(0xAD,0xAD,0xBD,0xBD));
		mpKeys.put("period",new KeyCodes(0xAE,0xAE,0xBE,0xBE));
		mpKeys.put("slash",new KeyCodes(0xAF,0xAF,0xBF,0xBF));
		mpKeys.put("n0",new KeyCodes(0xB0,0xB0,0xB0,0xB0));
		mpKeys.put("n1",new KeyCodes(0xB1,0xB1,0xA1,0xA1));
		mpKeys.put("n2",new KeyCodes(0xB2,0xB2,0xA2,0xA2));
		mpKeys.put("n3",new KeyCodes(0xB3,0xB3,0xA3,0xA3));
		mpKeys.put("n4",new KeyCodes(0xB4,0xB4,0xA4,0xA4));
		mpKeys.put("n5",new KeyCodes(0xB5,0xB5,0xA5,0xA5));
		mpKeys.put("n6",new KeyCodes(0xB6,0xB6,0xA6,0xA6));
		mpKeys.put("n7",new KeyCodes(0xB7,0xB7,0xA7,0xA7));
		mpKeys.put("n8",new KeyCodes(0xB8,0xB8,0xA8,0xA8));
		mpKeys.put("n9",new KeyCodes(0xB9,0xB9,0xA9,0xA9));
		mpKeys.put("colon",new KeyCodes(0xBA,0xBA,0xAA,0xAA));
		mpKeys.put("semicolon",new KeyCodes(0xBB,0xBB,0xAB,0xAB));
		mpKeys.put("A",new KeyCodes(0xC1,0x81,0xC1,0x81));
		mpKeys.put("B",new KeyCodes(0xC2,0x82,0xC2,0x82));
		mpKeys.put("C",new KeyCodes(0xC3,0x83,0xC3,0x83));
		mpKeys.put("D",new KeyCodes(0xC4,0x84,0xC4,0x84));
		mpKeys.put("E",new KeyCodes(0xC5,0x85,0xC5,0x85));
		mpKeys.put("F",new KeyCodes(0xC6,0x86,0xC6,0x86));
		mpKeys.put("G",new KeyCodes(0xC7,0x87,0xC7,0x87));
		mpKeys.put("H",new KeyCodes(0xC8,0x88,0xC8,0x88));
		mpKeys.put("left",new KeyCodes(0x88,0x88,0x88,0x88));
		mpKeys.put("I",new KeyCodes(0xC9,0x89,0xC9,0x89));
		mpKeys.put("J",new KeyCodes(0xCA,0x8A,0xCA,0x8A));
		mpKeys.put("K",new KeyCodes(0xCB,0x8B,0xCB,0x8B));
		mpKeys.put("L",new KeyCodes(0xCC,0x8C,0xCC,0x8C));
		mpKeys.put("M",new KeyCodes(0xCD,0x8D,0xDD,0x9D));
		mpKeys.put("return",new KeyCodes(0x8D,0x8D,0x8D,0x8D));
		mpKeys.put("N",new KeyCodes(0xCE,0x8E,0xDE,0x9E));
		mpKeys.put("O",new KeyCodes(0xCF,0x8F,0xCF,0x8F));
		mpKeys.put("P",new KeyCodes(0xD0,0x90,0xC0,0x80));
		mpKeys.put("Q",new KeyCodes(0xD1,0x91,0xD1,0x91));
		mpKeys.put("R",new KeyCodes(0xD2,0x92,0xD2,0x92));
		mpKeys.put("S",new KeyCodes(0xD3,0x93,0xD3,0x93));
		mpKeys.put("T",new KeyCodes(0xD4,0x94,0xD4,0x94));
		mpKeys.put("U",new KeyCodes(0xD5,0x95,0xD5,0x95));
		mpKeys.put("right",new KeyCodes(0x95,0x95,0x95,0x95));
		mpKeys.put("V",new KeyCodes(0xD6,0x96,0xD6,0x96));
		mpKeys.put("W",new KeyCodes(0xD7,0x97,0xD7,0x97));
		mpKeys.put("X",new KeyCodes(0xD8,0x98,0xD8,0x98));
		mpKeys.put("Y",new KeyCodes(0xD9,0x99,0xD9,0x99));
		mpKeys.put("Z",new KeyCodes(0xDA,0x9A,0xDA,0x9A));
		mpKeys.put("esc",new KeyCodes(0x9B,0x9B,0x9B,0x9B));
	}
	public void onKeyPress(final View key) {
		final String tag = key.getTag().toString();
		final KeyCodes codes = mpKeys.get(tag);
		if (codes != null) {
			final int a = codes.get(this.shift, this.control)&0x7F;
			Log.i(LOG_TAG,"ascii: "+Integer.toHexString(a));
			this.keypresses.put(a);
		}
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
