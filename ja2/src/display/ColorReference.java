/*
 * Created on Feb 10, 2008
 */
package display;

import java.util.Arrays;

class ColorReference
{
	private final int[] cb = new int[AppleNTSC.CB_END-AppleNTSC.CB_START-AnalogTV.CB_EXTRA];
	private final int hash;

	public ColorReference(final int[] cb, final int offset)
	{
		System.arraycopy(cb,offset,this.cb,0,this.cb.length);
		this.hash = getHash();
	}

	public int get(int i) { return this.cb[i]; }

	@Override
	public int hashCode()
	{
		return this.hash;
	}

	private int getHash()
	{
		int h = 7;
		for (final int x: this.cb)
		{
			h *= 31;
			h += x;
		}
		return h;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof ColorReference))
		{
			return false;
		}
		final ColorReference that = (ColorReference)obj;
		return Arrays.equals(that.cb,this.cb);
	}

	private static final int PERIOD = 4;
	public double[] getPhase()
	{
		final double[] phase = new double[PERIOD];
		for (int i = 0; i < this.cb.length; ++i)
		{
			phase[i % PERIOD] += this.cb[i];
		}
		double tot = 0;
		for (int i = 0; i < PERIOD; ++i)
		{
			tot += phase[i] * phase[i];
		}
		final double tsrt = StrictMath.sqrt(tot);
		for (int i = 0; i < PERIOD; i++)
		{
			phase[i] /= tsrt;
		}
		return phase;
	}

	public boolean isColor()
	{
		final double[] cb_phase = getPhase();
		final double cb_i = cb_phase[2]-cb_phase[0];
		final double cb_q = cb_phase[3]-cb_phase[1];
		return AnalogTV.COLOR_THRESH < cb_i*cb_i + cb_q*cb_q;
	}
}
