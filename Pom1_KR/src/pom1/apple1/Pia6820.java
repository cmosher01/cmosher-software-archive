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

	public Pia6820(Screen screen)
	{
		this.screen = screen;
		reset();
	}

//	public void setKbdInterrups(boolean b)
//	{
//		kbdInterrups = b;
//	}

	public boolean getKbdInterrups()
	{
		return kbdInterrups;
	}

	public boolean getDspOutput()
	{
		return dspOutput;
	}

	public void writeDspCr(int dspCr)
	{
		if (!dspOutput)
		{
			if ((dspCr & 0x80) != 0)
			{
				dspOutput = true;
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
		screen.outputDsp(dsp);
		this.dsp = dsp;
	}

	public void writeKbdCr(int kbdCr)
	{
		if (!kbdInterrups)
		{
			if ((kbdCr & 0x80) != 0)
			{
				kbdInterrups = true;
			}
			this.kbdCr = 0;
		}
		else
		{
			this.kbdCr = kbdCr;
		}
	}

	public void writeKbd(int kbd)
	{
		while ((this.kbdCr & 0x80) != 0)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		this.kbd = kbd;
	}

	public int readDspCr()
	{
		return dspCr;
	}

	public int readDsp()
	{
		return dsp;
	}

	public int readKbdCr()
	{
		if (kbdInterrups && (kbdCr & 0x80) != 0)
		{
			kbdCr = 0;
			return 0xA7;
		}
		return kbdCr;
	}

	public int readKbd()
	{
		return kbd;
	}

	public void reset()
	{
		kbdInterrups = false;
		kbdCr = 0;
		dspOutput = false;
		dspCr = 0;
	}
}
