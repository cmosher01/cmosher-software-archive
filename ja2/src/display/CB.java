/*
 * Created on Feb 10, 2008
 */
package display;

class CB
	{
		private final int[] cb = new int[AppleNTSC.CB_END-AppleNTSC.CB_START-AnalogTV.CB_EXTRA];
		private final int hash;

		public CB(final int[] cb)
		{
			if (cb.length != this.cb.length)
			{
				throw new IllegalArgumentException();
			}
			System.arraycopy(cb,0,this.cb,0,this.cb.length);
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
			if (!(obj instanceof CB))
			{
				return false;
			}
			final CB that = (CB)obj;
			if (this.cb.length != that.cb.length)
			{
				return false;
			}
			for (int i = 0; i < this.cb.length; ++i)
			{
				if (this.cb[i] != that.cb[i])
				{
					return false;
				}
			}
			return true;
		}
		private int length()
		{
			return this.cb.length;
		}
		public double[] getPhase()
		{
			final double[] phase = new double[4];
			for (int i = 0; i < length(); ++i)
			{
				phase[i & 3] += this.cb[i];
			}
			double tot = 0;
			for (int i = 0; i < 4; ++i)
			{
				tot += phase[i] * phase[i];
			}
			final double tsrt = Math.sqrt(tot);
			for (int i = 0; i < 4; i++)
			{
				phase[i] /= tsrt;
			}
//			if (tot > 0)
//			System.out.printf("phase: %f,%f,%f,%f\n",phase[0],phase[1],phase[2],phase[3]);
			return phase;
		}
		public boolean isColor()
		{
			final double[] cb_phase = getPhase();
			final double cb_i = cb_phase[2]-cb_phase[0];
			final double cb_q = cb_phase[3]-cb_phase[1];
			return AnalogTV.COLOR_THRESH < cb_i*cb_i + cb_q*cb_q;
//			int tot = 0;
//			for (int i = 0; i < length(); ++i)
//			{
//				final int icb = this.cb[i];
//				if (icb < 0)
//					tot += -icb;
//				else
//					tot += icb;
//			}
//			return 40 < tot;//220 < tot && tot < 260;
		}
	}