import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Event;
import java.awt.Label;
import java.awt.Panel;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.ContinuousAudioDataStream;

public class Applelet extends Applet implements Runnable
{
	private static final int DBG_CPU = 1;

	private EmAppleII emu;
	private DiskII disk;
	private String diskimage[] = new String[2];
	private Thread runthread;
	private AppleDisplay display;
	private int breakpoint;
	private Button reset_btn, pause_btn, clear_btn;
	private Checkbox trace_box;
	private Panel btnbar;
//	private SpeedReporter speedreport;
//	private Thread speedthread;
	// for sound
	private boolean audioPlaying = false;
	private static final int SOUNDBUFLEN = 2048;
	private byte samples[] = new byte[SOUNDBUFLEN];
	private AudioData sampledata = new AudioData(samples);
	private AudioDataStream sampleStream = new ContinuousAudioDataStream(sampledata);
	private int samplepos = 0;

//	public class SpeedReporter extends Label implements Runnable
//	{
//		Em6502 emu65;
//		int delay;
//
//		public SpeedReporter(Em6502 theemu, int thedelay)
//		{
//			super("        0 MHz");
//			emu65 = theemu;
//			delay = thedelay;
//		}
//
//		public void run()
//		{
//			int oldclock, newclock;
//			oldclock = emu65.clock;
//			while (true)
//			{
//				try
//				{
//					Thread.sleep(delay);
//					newclock = emu65.clock;
//					float cpuspeed = (newclock - oldclock) / delay;
//					setText(cpuspeed / 1000 + " MHz");
//					oldclock = newclock;
//				}
//				catch (InterruptedException e)
//				{
//				}
//			}
//		}
//	}

	public void init()
	{
		emu = new EmAppleII();
		disk = new DiskII(emu);
		emu.slots[6] = disk;
		display = new AppleDisplay();
		setLayout(new BorderLayout(0,0));
		btnbar = new Panel();
		reset_btn = new Button("Reset");
		btnbar.add("Top",reset_btn);
		clear_btn = new Button("Clear");
		btnbar.add("Top",clear_btn);
		pause_btn = new Button("Continue");
		pause_btn.disable();
		btnbar.add("Top",pause_btn);
		trace_box = new Checkbox("Trace");
		btnbar.add("Top",trace_box);
//		speedreport = new SpeedReporter(emu,5000);
//		btnbar.add("Top",speedreport);
//		speedthread = new Thread(speedreport);
//		speedthread.setPriority(Thread.NORM_PRIORITY + 1);
		add("South",btnbar);
		add("Center",display);
	}

	void loadROMs(String filename)
	{
		System.out.println("Loading ROMs from " + filename);
		try
		{
			URL url = new URL(getCodeBase(),filename);
			InputStream in = url.openStream();
			DataInputStream s = new DataInputStream(in);
			s.readFully(emu.mem,0xD000,0x3000);
			byte chrrom[] = new byte[1024];
			url = new URL(getCodeBase(),"charrom.bin");
			in = url.openStream();
			s = new DataInputStream(in);
			s.readFully(chrrom,0,1024);
			display.setCharROM(chrrom);
		}
		catch (MalformedURLException e)
		{
		}
		catch (IOException e)
		{
			System.out.println("Could not load ROMs");
		}
	}

	void loadDisk(String filename)
	{
		System.out.println("Loading disk image " + filename);
		try
		{
			URL url = new URL(getDocumentBase(),filename);
			DataInputStream s = new DataInputStream(url.openStream());
			byte buf[] = new byte[0x1000];
			for (int trk = 0; trk < 35; trk++)
			{
				s.readFully(buf,0,0x1000);
				disk.data[0][trk] = DiskII.nibblizeTrack(254,trk,buf);
			}
		}
		catch (MalformedURLException e)
		{
		}
		catch (IOException e)
		{
			System.out.println("Could not load disk image");
		}
	}

	void getParameters()
	{
		String param;
		param = getParameter("BREAK");
		if (param != null)
			breakpoint = Integer.parseInt(param);
		param = getParameter("DEBUG");
		if (param != null)
			emu.debugflags = Integer.parseInt(param);
		param = getParameter("REFRESHRATE");
		if (param != null)
			display.refresh_interval = Integer.parseInt(param);
		diskimage[0] = getParameter("DISK1");
		diskimage[1] = getParameter("DISK2");
	}

	public void start()
	{
		breakpoint = -1;
		getParameters();
		loadROMs("apple2.bin");
		loadDisk(diskimage[0]);
		display.apple = emu;
//		speedthread.start();
		try
		{
			AudioPlayer.player.start(sampleStream);
			audioPlaying = true;
		}
		catch (Throwable e)
		{
			System.err.println("Exception during audio init: " + e);
		}
	}

	public void stop()
	{
		if (audioPlaying)
		{
			try
			{
				AudioPlayer.player.stop(sampleStream);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		printProfileInfo();
		// ** change these stop()'s
//		speedthread.stop();
		if (runthread != null)
			runthread.stop();
		if (display != null)
			display.stop();
	}

	void printCPUInfo()
	{
		System.out.println("clk: " + Integer.toString(emu.clock,16) + " PPC: " + Integer.toString(emu.PPC,16) + " op: " + Integer.toString(emu.readMemory(emu.PPC),16) + "  A: "
			+ Integer.toString(emu.A,16) + "  X: " + Integer.toString(emu.X,16) + "  Y: " + Integer.toString(emu.Y,16) + " fl: " + Integer.toString(emu.P,16) + " sp: " + Integer.toString(emu.S,16));
	}

	void addWaveSample()
	{
		samples[samplepos++ & (SOUNDBUFLEN - 1)] = (byte)(emu.soundstate << 5);
	}

	public void run()
	{
		System.out.println("Reset!");
		emu.reset();
		System.out.println("PPC = " + Integer.toString(emu.PPC,16));
		long t1 = System.currentTimeMillis();
		while (true)
		{
			if (!emu.doDebug(DBG_CPU) && breakpoint < 0)
			{
				// after this loop, we should execute
				// 100,000 clock cycles
				// (unless we're in breakpoint mode)
				int clockend = emu.clock;
				for (int iters = 0; iters < 781; iters++)
				{
					// special fast version of loop
					// 46 instructions per wave sample
					// 128 for 8000 Hz
					// emu.executeInstructions(64)
					// int clockend = ((emu.clock >> 7)+1) << 7;
					clockend += 128;
					// max clock/inst is 8 cycles, so 8*16 = 128, so OK
					while (emu.clock < clockend)
					{
						emu.executeInstructions(1 + ((clockend - emu.clock) >> 3));
					}
					addWaveSample();
				}
				// govern the speed
				// every 100 ms, should execute 100,000 cycles
				long t2 = System.currentTimeMillis();
				try
				{
					if (t2 - t1 < 100)
						Thread.sleep(100 - t2 + t1);
					Thread.yield();
				}
				catch (Exception e)
				{
				}
				t1 += 100;
			}
			else
			{
				if (emu.doDebug(DBG_CPU))
					printCPUInfo();
				if (emu.PPC == breakpoint)
				{
					System.out.println("Breakpoint");
					for (int i = 0; i < 20; i++)
					{
						printCPUInfo();
						emu.executeInstruction();
					}
					pause_btn.enable();
					runthread.suspend();
				}
				emu.executeInstruction();
			}
		}
	}

	void printProfileInfo()
	{
		for (int i = 0; i < 256; i++)
		{
			if (emu.profile[i] > 5000)
				System.out.println(Integer.toString(i,16) + "\t" + emu.profile[i]);
			if ((i & 15) == 15)
				System.out.println();
		}
	}

	public boolean handleEvent(Event evt)
	{
		switch (evt.id)
		{
			case Event.ACTION_EVENT:
				if (evt.target == reset_btn)
				{
					try
					{
						if (runthread != null)
						{
							runthread.stop();
							runthread.join();
						}
					}
					catch (InterruptedException e)
					{
					}
					// printProfileInfo();
					runthread = new Thread(this);
					runthread.start();
					return true;
				}
				else if (evt.target == clear_btn)
				{
					emu.clearProfile();
				}
				else if (evt.target == pause_btn)
				{
					if (runthread != null)
						runthread.resume();
				}
				else if (evt.target == trace_box)
				{
					if (emu != null)
					{
						if (((Boolean)evt.arg).booleanValue())
							emu.debugflags |= DBG_CPU;
						else
							emu.debugflags &= ~DBG_CPU;
					}
				}
		}
		return super.handleEvent(evt);
	}
}
