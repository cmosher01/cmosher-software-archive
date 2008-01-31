package video;

import gui.UI;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Created on Jan 18, 2008
 */
public class AnalogTV implements VideoDisplayDevice
{
	private final DataBuffer imageBuf;
	private final UI ui;

	private final AtomicBoolean on = new AtomicBoolean();
	private final int[] signal = new int[AppleNTSC.SIGNAL_LEN];
	int isig;



	/**
	 * @param image
	 * @param ui
	 */
	public AnalogTV(final BufferedImage image, final UI ui)
	{
		this.imageBuf = image.getRaster().getDataBuffer();
		this.ui = ui;
	}



	public void dump()
	{
		int pi = 0;
		final int[] yiq = new int[AppleNTSC.H];
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			final CB cb = get_cb(lineno);
			final IQ iq_factor = get_iq_factor(cb);
			ntsc_to_yiq(lineno*AppleNTSC.H,AppleNTSC.H,iq_factor,yiq);
			if (pi==0)
				System.out.printf("%3d:--------------------------------------------------------------------------\n",lineno);
			for (int colno = 0; colno < AppleNTSC.H; ++colno)
			{
				System.out.print(pi==AppleNTSC.PIC_START ? "|" : " ");
				final int sig = this.signal[lineno*AppleNTSC.H+colno];
				System.out.printf(" %+04d",sig);
				final int yiqv = yiq[colno];
				int y = (yiqv&0xFF)-140;
				int i = ((yiqv>>8)&0xFF)-140;
				int q = ((yiqv>>16)&0xFF)-140;
				System.out.printf("(%+04d,%+04d,%+04d)",y,i,q);
				final int rgb = yiq2rgb(yiq[colno]);
				final int r = (rgb >> 16) & 0xff;
				final int g = (rgb >> 8) & 0xff;
				final int b = (rgb ) & 0xff;
				System.out.printf("[%06X:%03d,%03d,%03d]",rgb,r,g,b);

				++pi;
				if (pi >= AppleNTSC.H)
				{
					System.out.println();
					pi = 0;
				}
			}
		}
	}



	public void putSignal(final int level)
	{
		if (this.isig >= AppleNTSC.SIGNAL_LEN)
		{
			throw new IllegalStateException("At end of screen; must re-synch before writing any more signal");
		}
		this.signal[this.isig++] = level;
		if (this.isig == AppleNTSC.SIGNAL_LEN)
		{
			if (isOn())
			{
				this.draw_visible();
			}
			this.isig = 0;
		}
	}







	private void draw()
	{
		int pi = 0;
		final int[] yiq = new int[AppleNTSC.H];
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			final CB cb = get_cb(lineno);
			final IQ iq_factor = get_iq_factor(cb);
			ntsc_to_yiq(lineno*AppleNTSC.H,AppleNTSC.H,iq_factor,yiq);
			for (int colno = 0; colno < AppleNTSC.H; ++colno)
			{
				final int rgb = yiq2rgb(yiq[colno]);
				this.imageBuf.setElem(pi,rgb);
				this.imageBuf.setElem(pi + AppleNTSC.H,rgb);

				++pi;
				if (pi % AppleNTSC.H == 0)
				{
					pi += AppleNTSC.H;
				}
			}
		}
		this.ui.updateScreen();
	}

	private void draw_signal()
	{
		int pi = 0;
		for (int i = 0; i < this.signal.length; ++i)
		{
			final int ire = (int)Math.rint(this.signal[i]) - AppleNTSC.SYNC_LEVEL;
			final int val = (int)Math.rint(ire * 255.0 / (AppleNTSC.WHITE_LEVEL - AppleNTSC.SYNC_LEVEL));
			final int rgb = (val << 16) | (val << 8) | (val);
			this.imageBuf.setElem(pi,rgb);
			this.imageBuf.setElem(pi+AppleNTSC.H,rgb);
			++pi;
			if (pi % AppleNTSC.H == 0)
			{
				pi += AppleNTSC.H;
			}
		}
		this.ui.updateScreen();
	}

	private void draw_visible_signal()
	{
		for (int row = 0; row < 192; ++row)
		{
			for (int col = 350; col < AppleNTSC.H-2; ++col)
			{
				final int is = row*AppleNTSC.H+col;
				final int ire = this.signal[is];
				final int val = (int)Math.rint(ire * 255.0 / AppleNTSC.WHITE_LEVEL);
				final int rgb = (val << 16) | (val << 8) | (val);
				final int ip = row*(AppleNTSC.H-2-350)*2+(col-350);
				this.imageBuf.setElem(ip,rgb);
				this.imageBuf.setElem(ip+(AppleNTSC.H-2-350),rgb);
			}
		}
		this.ui.updateScreen();
	}

	private void draw_visible()
	{
		final int[] yiq = new int[AppleNTSC.H];
		for (int row = 0; row < 192; ++row)
		{
			final CB cb = get_cb(row);
			final IQ iq_factor = get_iq_factor(cb);
			ntsc_to_yiq(row*AppleNTSC.H+350,AppleNTSC.H-2-350,iq_factor,yiq);
			for (int col = 350; col < AppleNTSC.H-2; ++col)
			{
				final int rgb = yiq2rgb(yiq[col-350]);
				final int ip = row*(AppleNTSC.H-2-350)*2+(col-350);
				this.imageBuf.setElem(ip,rgb);
				this.imageBuf.setElem(ip+(AppleNTSC.H-2-350),rgb);
			}
		}
		this.ui.updateScreen();
	}







	private static class IQ
	{
		private final double[] iq = new double[4];
		public IQ(){}
		public IQ(final double[] iq)
		{
			if (iq.length != this.iq.length)
			{
				throw new IllegalArgumentException();
			}
			System.arraycopy(iq,0,this.iq,0,this.iq.length);
		}
		public double get(int i) { return this.iq[i]; }
	}

	private static final int CB_EXTRA = 32;
	private static class CB
	{
		private final int[] cb = new int[AppleNTSC.CB_END-AppleNTSC.CB_START-CB_EXTRA];
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
		public int length()
		{
			return this.cb.length;
		}
	}




	private static final double PH = 127.0/128.0;
	private double[] get_cb_phase(final CB cb)
	{
		final double[] phase = new double[4];
		for (int i = 0; i < cb.length(); ++i)
		{
			phase[i & 3] *= PH;
			phase[i & 3] += cb.get(i);
		}
		double tot = .1;
		for (int i = 0; i < 4; ++i)
		{
			tot += phase[i] * phase[i];
		}
		final double cbgain = 32.0 / Math.sqrt(tot);
		for (int i = 0; i < 4; i++)
		{
			phase[i] *= cbgain;
		}
		return phase;
	}

	private final int[] rcb = new int[AppleNTSC.CB_END-AppleNTSC.CB_START-CB_EXTRA];
	private CB get_cb(int lineno)
	{
		final int isp = lineno * AppleNTSC.H;
		for (int i = AppleNTSC.CB_START + CB_EXTRA/2; i < AppleNTSC.CB_END - CB_EXTRA/2; ++i)
		{
			this.rcb[i-(AppleNTSC.CB_START + CB_EXTRA/2)] = this.signal[isp + i];
		}
		return new CB(this.rcb);
	}


	private static final Map<CB,IQ> cacheCB = new HashMap<CB,IQ>(2,1);

	private static final double COLOR_THRESH = 2.8;
	private static final double IQ_OFFSET_DEGREES = -33;
	private static final double TINT_I = -Math.cos(IQ_OFFSET_DEGREES * Math.PI / 180);
	private static final double TINT_Q = +Math.sin(IQ_OFFSET_DEGREES * Math.PI / 180);

	private static final IQ BLACK_AND_WHITE = new IQ();

	private IQ get_iq_factor(final CB cb)
	{
		if (cacheCB.containsKey(cb))
		{
			return cacheCB.get(cb);
		}
		final double[] cb_phase = get_cb_phase(cb);
		final double cb_i = (cb_phase[2] - cb_phase[0]) / 16;
		final double cb_q = (cb_phase[3] - cb_phase[1]) / 16;
		if (cb_i*cb_i + cb_q*cb_q < COLOR_THRESH)
		{
			return BLACK_AND_WHITE;
		}
//		System.out.printf("%+8.2f,%+8.2f,%+8.2f,%+8.2f\n",cb_phase[0],cb_phase[1],cb_phase[2],cb_phase[3]);

		final double[] iq_factor = new double[4];

		iq_factor[0] = cb_i * TINT_I - cb_q * TINT_Q;
		iq_factor[2] = -iq_factor[0];
		iq_factor[1] = cb_q * TINT_I + cb_i * TINT_Q;
		iq_factor[3] = -iq_factor[1];

//		System.out.printf("%+8.2f,%+8.2f,%+8.2f,%+8.2f\n",iq_factor[0],iq_factor[1],iq_factor[2],iq_factor[3]);
		final IQ iq = new IQ(iq_factor);
//		System.out.println("adding to cb cache"); // TODO don't add to cache while displaying static
		cacheCB.put(cb,iq);
		return iq;
	}

	private void ntsc_to_yiq(final int isignal, final int siglen, final IQ iq_factor, final int[] yiq)
	{
		final Lowpass_3_58_MHz filterY = new Lowpass_3_58_MHz();
		final Lowpass_1_5_MHz filterI = new Lowpass_1_5_MHz();
		final Lowpass_1_5_MHz filterQ = new Lowpass_1_5_MHz();
		for (int off = 0; off < siglen; ++off)
		{
			final int sig = this.signal[isignal + off];
			final int y = filterY.transition(sig); // + 40; // to show blacker-than-black levels
			final int i;
			final int q;
			if (y < -2)
			{
				i = 0;
				q = 0;
			}
			else
			{
				i = filterI.transition((int)(sig * iq_factor.get(off & 3)));
				q = filterQ.transition((int)(sig * iq_factor.get((off + 3) & 3)));
			}

			yiq[off] = (((q+140)&0xff) << 16) | (((i+140)&0xff) << 8) | ((y+140)&0xff);
		}
	}

	private static int yiq2rgb(final int yiq)
	{
		double r = ((yiq&0xFF)-140) + 0.956 * (((yiq>>8)&0xFF)-140) + 0.621 * (((yiq>>16)&0xFF)-140);
		double g = ((yiq&0xFF)-140) - 0.272 * (((yiq>>8)&0xFF)-140) - 0.647 * (((yiq>>16)&0xFF)-140);
		double b = ((yiq&0xFF)-140) - 1.105 * (((yiq>>8)&0xFF)-140) + 1.702 * (((yiq>>16)&0xFF)-140);

//		r *= 1.3;
//		g *= 1.3;
//		b *= 1.3;

		final int rgb =
			(calc_color(r) << 16)| 
			(calc_color(g) <<  8)| 
			(calc_color(b) <<  0);

		return rgb;
	}

	private static int calc_color(final double color)
	{
		int x = (int)(color * 0x100 / AppleNTSC.LEVEL_RANGE + .5);
		x = clamp(0,x,0x100);
		return x & 0xFF;
	}

	private static int clamp(int min, int x, int lim)
	{
		if (x < min)
			return min;
		if (lim <= x)
			return lim-1;
		return x;
	}

	public boolean isOn()
	{
		synchronized (this.on)
		{
			return this.on.get();
		}
	}

	public void powerOn(final boolean on)
	{
		synchronized (this.on)
		{
			this.on.set(on);
		}
		if (!isOn())
		{
			drawBlankScreen();
		}
	}

	private void drawBlankScreen()
	{
		for (int i = 0; i < AppleNTSC.SIGNAL_LEN*2; ++i)
			this.imageBuf.setElem(i,0);
	}
}
