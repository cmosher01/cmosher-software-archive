package pom1.apple1;

public class Pia6820
{
	private final Screen screen;

	private int dspCr;
	private int dsp;
	private boolean dspOutput;

	private int kbdCr;
	private int kbd;
	private boolean kbdInterrups;

	public Pia6820(Screen screen)
	{
		this.screen = screen;
		reset();
	}

	public void setKbdInterrups(boolean b)
	{
		kbdInterrups = b;
	}

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
		while (this.kbdCr != 0)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
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
