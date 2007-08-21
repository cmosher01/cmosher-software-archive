package pom1.apple1;

public class Pia6820
{
	private final Screen screen;

	private volatile int dspCr;
	private volatile int dsp;
	private volatile boolean dspOutput;

	private volatile int kbdCr;
	private volatile int kbd;
	private volatile boolean kbdInterrups;

	private volatile Keyboard keyboard;

	public Pia6820(final Screen screen, final Keyboard keyboard)
	{
		this.screen = screen;
		this.keyboard = keyboard;
		reset();
	}

	public void reset()
	{
		kbdInterrups = false;
		kbdCr = 0;
		dspOutput = false;
		dspCr = 0;
	}

	public boolean getKbdInterrups()
	{
		return kbdInterrups;
	}

	public boolean getDspOutput()
	{
		return dspOutput;
	}





	public int readDspCr()
	{
		return this.dspCr;
	}

	public int readDsp()
	{
		return this.dsp;
	}

	public void writeDspCr(int dspCr)
	{
		if (!this.dspOutput)
		{
			if ((dspCr & 0x80) != 0)
			{
				this.dspOutput = true;
			}
			this.dspCr = 0;
		}
		else
		{
			this.dspCr = dspCr;
		}
	}

	public void writeDsp(int dsp)
	{
		dsp &= 0x7F;
		this.screen.outputDsp(dsp);
		this.dsp = dsp;
	}





	public int readKbdCr()
	{
//		if (kbdInterrups && (kbdCr & 0x80) != 0)
//		{
//			kbdCr = 0;
//			return 0xA7;
//		}
//		return kbdCr;

		final int kbdCr = this.kbdCr;
		if (this.kbdInterrups && this.keyboard.isReady())
		{
			this.kbdCr = 0;
		}
		return kbdCr;
	}

	public int readKbd()
	{
		return this.keyboard.getNextKey();
	}

	public void writeKbdCr(int kbdCr)
	{
		if (!this.kbdInterrups)
		{
			if ((kbdCr & 0x80) != 0)
			{
				this.kbdInterrups = true;
			}
		}
		this.kbdCr = kbdCr;
	}

	public void writeKbd(int kbd)
	{
		System.err.println("Write to KBD not supported");
	}
}
