package pom1.apple1;

public class Pia6820
{
	private final Screen screen;

	private volatile int dspCr;
	private volatile int dsp;

	private volatile int kbdCr;
	private volatile int kbd;

	private volatile Keyboard keyboard;

	private int prevKey;

	private int kbdCrReady;

	public Pia6820(final Screen screen, final Keyboard keyboard)
	{
		this.screen = screen;
		this.keyboard = keyboard;
		reset();
	}

	public void reset()
	{
		this.kbdCr = 0;
		this.dspCr = 0;
	}





	public int readDspCr()
	{
		return this.dspCr;
	}

	public int readDsp()
	{
		return this.dsp;
	}

	public void writeDspCr(final int dspCr)
	{
		this.dspCr = dspCr;
	}

	public void writeDsp(int dsp)
	{
		dsp &= 0x7F;
		this.screen.outputDsp(dsp);
		this.dsp = dsp;
	}





	public int readKbdCr()
	{
		int kbdCr = this.kbdCr;
		if (this.keyboard.isReady())
		{
			kbdCr = this.kbdCrReady;
			this.kbdCr = 0;
		}
		return kbdCr;
	}

	public int readKbd() throws InterruptedException
	{
		if (this.keyboard.isReady())
		{
			final int c = this.keyboard.getNextKey();
			this.prevKey = c & 0x7F;
			return c;
		}
		return this.prevKey;
	}

	public void writeKbdCr(final int kbdCr)
	{
		this.kbdCrReady = kbdCr;
	}

	public void writeKbd(final int kbd)
	{
		System.err.println("Write to KBD not supported");
	}
}
