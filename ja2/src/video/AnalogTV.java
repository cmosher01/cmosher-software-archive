package video;

import gui.UI;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Created on Jan 18, 2008
 */
public class AnalogTV implements VideoDisplayDevice
{
	private final ScreenImage image;

	private final AtomicBoolean on = new AtomicBoolean();
	private final int[] signal = new int[AppleNTSC.SIGNAL_LEN];
	int isig;



	/**
	 * @param image
	 * @param ui
	 */
	public AnalogTV(final ScreenImage image)
	{
		this.image = image;
	}



	public void dump()
	{
		final int[] yiq = new int[AppleNTSC.H];
		for (int row = 0; row < 192; ++row)
		{
			final CB cb = get_cb(row);
			final IQ iq_factor = get_iq_factor(cb);
			ntsc_to_yiq(row*AppleNTSC.H+350,AppleNTSC.H-2-350,iq_factor,yiq);
			for (int col = 350; col < AppleNTSC.H-2; ++col)
			{
				final int sig = this.signal[row*AppleNTSC.H+col];
				System.out.printf(" %+04d",sig);
				final int yiqv = yiq[col-350];
				int y = (yiqv&0xFF)-IQINTOFF;
				int i = ((yiqv>>8)&0xFF)-IQINTOFF;
				int q = ((yiqv>>16)&0xFF)-IQINTOFF;
				System.out.printf("(%+04d,%+04d,%+04d)",y,i,q);

				final int rgb = yiq2rgb(yiqv);
				final int r = (rgb >> 16) & 0xff;
				final int g = (rgb >> 8) & 0xff;
				final int b = (rgb ) & 0xff;
				System.out.printf("[%06X:%03d,%03d,%03d]",rgb,r,g,b);
			}
			System.out.println();
		}

//		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
//		{
//			final CB cb = get_cb(lineno);
//			final IQ iq_factor = get_iq_factor(cb);
//			ntsc_to_yiq(lineno*AppleNTSC.H,AppleNTSC.H,iq_factor,yiq);
//			for (int colno = 0; colno < AppleNTSC.H; ++colno)
//			{
//				System.out.print(pi==AppleNTSC.PIC_START ? "|" : " ");
//				final int sig = this.signal[lineno*AppleNTSC.H+colno];
//				System.out.printf(" %+04d",sig);
//				final int yiqv = yiq[colno];
//				int y = (yiqv&0xFF)-IQINTOFF;
//				int i = ((yiqv>>8)&0xFF)-IQINTOFF;
//				int q = ((yiqv>>16)&0xFF)-IQINTOFF;
//				System.out.printf("(%+04d,%+04d,%+04d)",y,i,q);
//				final int rgb = yiq2rgb(yiq[colno]);
//				final int r = (rgb >> 16) & 0xff;
//				final int g = (rgb >> 8) & 0xff;
//				final int b = (rgb ) & 0xff;
//				System.out.printf("[%06X:%03d,%03d,%03d]",rgb,r,g,b);
//
//				++pi;
//				if (pi >= AppleNTSC.H)
//				{
//					System.out.println();
//					pi = 0;
//				}
//			}
//		}
	}

	private boolean noise;
	private volatile DisplayType type;

	private int rrr = 1;
	public void putAsDisconnectedVideoIn()
	{
		this.noise = true;
		this.rrr *= 16807;
		this.rrr %= 0x7FFFFFFF;
		++this.rrr;
		putSignal((this.rrr>>>25)-27);
		this.noise = false;
	}

	public void restartSignal()
	{
		this.isig = 0;
	}

	public void putSignal(final int level)
	{
//		if (this.isig >= AppleNTSC.SIGNAL_LEN)
//		{
//			throw new IllegalStateException("At end of screen; must re-synch before writing any more signal");
//		}
		this.signal[this.isig++] = level;
		if (this.isig == AppleNTSC.SIGNAL_LEN)
		{
			if (isOn())
			{
				this.drawCurrent();
			}
			else
			{
				this.drawBlank();
			}
			this.isig = 0;
		}
	}







	private void drawCurrent()
	{
		switch (this.type)
		{
			case MONITOR_COLOR: drawMonitorColor(); break;
			case MONITOR_WHITE: drawMonitorWhite(); break;
			case MONITOR_GREEN: drawMonitorGreen(); break;
			case MONITOR_ORANGE: drawMonitorOrange(); break;
			case TV_OLD_COLOR: drawTVOld(); break;
			case TV_OLD_BW: drawTVOld(); break;
			case TV_NEW_COLOR: drawTVNew(); break;
			case TV_NEW_BW: drawTVNew(); break;
		}
	}



//	private void draw()
//	{
//		int pi = 0;
//		final int[] yiq = new int[AppleNTSC.H];
//		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
//		{
//			final CB cb = get_cb(lineno);
//			final IQ iq_factor = get_iq_factor(cb);
//			ntsc_to_yiq(lineno*AppleNTSC.H,AppleNTSC.H,iq_factor,yiq);
//			for (int colno = 0; colno < AppleNTSC.H; ++colno)
//			{
//				final int rgb = yiq2rgb(yiq[colno]);
//				this.image.setElem(pi,rgb);
//				this.image.setElem(pi + AppleNTSC.H,rgb);
//
//				++pi;
//				if (pi % AppleNTSC.H == 0)
//				{
//					pi += AppleNTSC.H;
//				}
//			}
//		}
//		this.image.notifyObservers();
//	}
//
//	private void draw_signal()
//	{
//		int pi = 0;
//		for (int i = 0; i < this.signal.length; ++i)
//		{
//			final int ire = (int)Math.rint(this.signal[i]) - AppleNTSC.SYNC_LEVEL;
//			final int val = (int)Math.rint(ire * 255.0 / (AppleNTSC.WHITE_LEVEL - AppleNTSC.SYNC_LEVEL));
//			final int rgb = (val << 16) | (val << 8) | (val);
//			this.image.setElem(pi,rgb);
//			this.image.setElem(pi+AppleNTSC.H,rgb);
//			++pi;
//			if (pi % AppleNTSC.H == 0)
//			{
//				pi += AppleNTSC.H;
//			}
//		}
//		this.image.notifyObservers();
//	}

	private static final int D_IP = AppleNTSC.H-2-350;
	private void drawMonitorColor()
	{
		final int[] rgb = new int[AppleNTSC.H];
		int ip = 0;
		for (int row = 0; row < 192; ++row)
		{
			final CB cb = get_cb(row);
			final boolean removeColor = (this.type == DisplayType.TV_NEW_BW || !cb.isColor());
			ntsc_to_rgb_monitor(row*AppleNTSC.H+350,D_IP,rgb);
			for (int col = 350; col < AppleNTSC.H-2; ++col)
			{
				int rgbv = rgb[col-350];
				if (removeColor && rgbv != 0)
				{
					rgbv = 0xFFFFFF;
				}
				this.image.setElem(ip,rgbv);
				this.image.setElem(ip+D_IP,rgbv); // display same pixel on next row
				++ip;
			}
			ip += D_IP;
		}
		this.image.notifyObservers();
	}

	private void drawMonitorWhite()
	{
		drawMonitorMonochrome(0xFFFFFF);
	}

	private void drawMonitorGreen()
	{
		drawMonitorMonochrome(0x00FF0D);
	}

	private void drawMonitorOrange()
	{
		drawMonitorMonochrome(0xFF8C00);
	}

	private void drawMonitorMonochrome(final int color)
	{
		int ip = 0;
		for (int row = 0; row < 192; ++row)
		{
			for (int col = 350; col < AppleNTSC.H-2; ++col)
			{
				final int is = row*AppleNTSC.H+col;
				final int rgb = this.signal[is] > 50 ? color : 0;
				this.image.setElem(ip,rgb);
				this.image.setElem(ip+D_IP,rgb);
				++ip;
			}
			ip += D_IP;
		}
		this.image.notifyObservers();
	}

	private void drawTVOld()
	{
		final int[] yiq = new int[AppleNTSC.H];
		int ip = 0;
		for (int row = 0; row < 192; ++row)
		{
			final IQ iq_factor;
			if (this.type == DisplayType.TV_OLD_COLOR)
			{
				final CB cb = get_cb(row);
				iq_factor = get_iq_factor(cb);
			}
			else
			{
				iq_factor = BLACK_AND_WHITE;
			}
			ntsc_to_yiq(row*AppleNTSC.H+350,AppleNTSC.H-2-350,iq_factor,yiq);
			for (int col = 350; col < AppleNTSC.H-2; ++col)
			{
				final int rgb = yiq2rgb(yiq[col-350]);
				this.image.setElem(ip,rgb);
				this.image.setElem(ip+D_IP,rgb);
				++ip;
			}
			ip += D_IP;
		}
		this.image.notifyObservers();
	}

	private void drawTVNew()
	{
		final int[] rgb = new int[AppleNTSC.H];
		int ip = 0;
		for (int row = 0; row < 192; ++row)
		{
			final CB cb = get_cb(row);
			final boolean removeColor = (this.type == DisplayType.TV_NEW_BW || !cb.isColor());
			ntsc_to_rgb_newtv(row*AppleNTSC.H+350,AppleNTSC.H-2-350,rgb);
			for (int col = 350; col < AppleNTSC.H-2; ++col)
			{
				int rgbv = rgb[col-350];
				if (removeColor)
				{
					rgbv = color2bw(rgbv);
				}
				this.image.setElem(ip,rgbv);
				this.image.setElem(ip+D_IP,rgbv);
				++ip;
			}
			ip += D_IP;
		}
		this.image.notifyObservers();
	}

	private void drawBlank()
	{
		this.image.setAllElem(0);
		this.image.notifyObservers();
	}





	private static final int[] hirescolor =
	{
		A2ColorsObserved.COLOR[A2ColorIndex.HIRES_GREEN.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.HIRES_ORANGE.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.HIRES_VIOLET.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.HIRES_BLUE.ordinal()],
	};

	private static final int[] loreslightcolor =
	{
		A2ColorsObserved.COLOR[A2ColorIndex.LIGHT_BROWN.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.LIGHT_MAGENTA.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.LIGHT_BLUE.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.LIGHT_BLUE_GREEN.ordinal()],
	};

	private static final int[] loresdarkcolor =
	{
		A2ColorsObserved.COLOR[A2ColorIndex.DARK_BLUE_GREEN.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.DARK_BROWN.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.DARK_MAGENTA.ordinal()],
		A2ColorsObserved.COLOR[A2ColorIndex.DARK_BLUE.ordinal()],
	};

	private void ntsc_to_rgb_monitor(final int isignal, final int siglen, int[] rgb)
	{
		// TODO: check for color burst and act accordingly
		int s0, s1, se;
		s0 = s1 = isignal;
		se = isignal+siglen;
		while (s1 < se)
		{
			// no signal (black)
			while (this.signal[s0] < 50 && s0<se) { rgb[s0-isignal] = 0; ++s0; }

			// signal (white, grey, or color)
			s1 = s0;
			while (this.signal[s1] > 50 && s1<se) { ++s1; }
			final int slen = s1-s0;
			int c = 0;
			if (slen >= 4)
			{
				c = 0xFFFFFF;
			}
			else if (slen == 1)
			{
				if (this.signal[s0-2] > 50 && this.signal[s0+2] > 50)
					c = 0xFFFFFF;
				else
					c = loresdarkcolor[s0 % 4];
			}
			else if (slen == 2)
			{
				c = hirescolor[s0 % 4];
			}
			else if (slen == 3)
			{
				c = loreslightcolor[s0 % 4];
			}
			else
			{
				++s1;
			}

			c |= 0xFF000000;

			for (int i = s0; i < s1; ++i)
				rgb[i-isignal] = c;
			s0 = s1;
		}
	}

	private void ntsc_to_rgb_newtv(final int isignal, final int siglen, int[] rgb)
	{
		int sp, s0, s1, se;
		sp = s0 = s1 = isignal;
		se = isignal+siglen;
		int c = 0;
		while (s1 < se)
		{
			// no signal; black...
			sp = s0;
			while (this.signal[s0] < 50 && s0<se) { rgb[s0-isignal] = 0; ++s0; }
			// unless it's too short, then color it (but not white)
			if (s0-sp < 4 && c != 0xFFFFFFFF)
			{
				for (int i = sp; i < s0; ++i)
					rgb[i-isignal] = c;
			}

			// signal (white, grey, or color)
			s1 = s0;
			while (this.signal[s1] > 50 && s1<se) { ++s1; }
			final int slen = s1-s0;
			if (slen >= 4)
			{
				c = 0xFFFFFF;
			}
			else if (slen == 1)
			{
				if (this.signal[s0-2] > 50 && this.signal[s0+2] > 50)
					c = 0x808080;
				else
					c = loresdarkcolor[s0 % 4];
			}
			else if (slen == 2)
			{
				c = hirescolor[s0 % 4];
			}
			else if (slen == 3)
			{
				c = loreslightcolor[s0 % 4];
			}
			else
			{
				++s1;
			}

			c |= 0xFF000000;

			for (int i = s0; i < s1; ++i)
				rgb[i-isignal] = c;
			s0 = s1;
		}
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
			if (tot > 0)
			System.out.printf("phase: %f,%f,%f,%f\n",phase[0],phase[1],phase[2],phase[3]);
			return phase;
		}
		public boolean isColor()
		{
			int tot = 0;
			for (int i = 0; i < length(); ++i)
			{
				final int icb = this.cb[i];
				if (icb < 0)
					tot += -icb;
				else
					tot += icb;
			}
			return 220 < tot && tot < 260;
		}
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

	private static final double COLOR_THRESH = 1.4;
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
		final double[] cb_phase = cb.getPhase();
		final double cb_i = (cb_phase[2]-cb_phase[0]);
		final double cb_q = (cb_phase[3]-cb_phase[1]);
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
		if (!this.noise)
		{
//			System.out.println("adding to cb cache");
			cacheCB.put(cb,iq);
		}
		return iq;
	}

	private static final int IQINTOFF = 130;
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

			yiq[off] = (((q+IQINTOFF)&0xff) << 16) | (((i+IQINTOFF)&0xff) << 8) | ((y+IQINTOFF)&0xff);
		}
	}

	private static int yiq2rgb(final int yiq)
	{
		double r = ((yiq&0xFF)-IQINTOFF) + 0.956 * (((yiq>>8)&0xFF)-IQINTOFF) + 0.621 * (((yiq>>16)&0xFF)-IQINTOFF);
		double g = ((yiq&0xFF)-IQINTOFF) - 0.272 * (((yiq>>8)&0xFF)-IQINTOFF) - 0.647 * (((yiq>>16)&0xFF)-IQINTOFF);
		double b = ((yiq&0xFF)-IQINTOFF) - 1.105 * (((yiq>>8)&0xFF)-IQINTOFF) + 1.702 * (((yiq>>16)&0xFF)-IQINTOFF);

		final int rgb =
			(calc_color(r) << 16)| 
			(calc_color(g) <<  8)| 
			(calc_color(b) <<  0);

		return rgb;
	}

	private static int color2bw(final int rgb)
	{
		final int y = rgb2y(rgb);
		return y<<16 | y<<8 | y;
	}

	private static int rgb2y(final int rgb) // y in range 0-255
	{
		return (int)((0.299*((rgb>>16)&0xFF) + 0.587*((rgb>>8)&0xFF) + 0.114*(rgb&0xFF))/1.04);
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
		this.image.notifyObservers();
	}



	public void setType(DisplayType type)
	{
		this.type = type;
	}
}
