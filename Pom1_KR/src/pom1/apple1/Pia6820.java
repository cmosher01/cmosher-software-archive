package pom1.apple1;

public class Pia6820
{
	public Pia6820(Screen screen)
	{
		this.screen = screen;
		echo = true;//System.getProperty("ECHO","N").equalsIgnoreCase("Y");
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
		if (echo)
		{
			if (dsp == 0x0D)
				System.out.println();
			else
				System.out.print((char)dsp);
		}
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
				this.kbdCr = 0;
			}
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

	private int dspCr;
	private int dsp;
	private int kbdCr;
	private int kbd;
	private boolean kbdInterrups;
	private boolean dspOutput;
	private Screen screen;
	private boolean echo;
}
