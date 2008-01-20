import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/*
 * Created on Jan 18, 2008
 */
public class AnalogTV
{
	private double agclevel = 1;
	private double color_control = .6;
	private analogtv_yiq[] yiq = new analogtv_yiq[AppleNTSC.SIGNAL_LEN];//[AppleNTSC.PIC_LEN + 10];
	private double[] cb_phase = new double[4];
	private double[][] line_cb_phase = new double[AppleNTSC.V][4];
	private double[] signal = new double[AppleNTSC.SIGNAL_LEN];

	public AnalogTV()
	{
		for (int i = 0; i < this.yiq.length; ++i)
		{
			this.yiq[i] = new analogtv_yiq();
		}
	}

	public void dump_signal()
	{
		int pi = 0;
		for (int i = 0; i < this.signal.length; ++i)
		{
			System.out.printf(" %+4d",this.signal[i]);
			++pi;
			if (pi >= AppleNTSC.H)
			{
				System.out.println();
				pi = 0;
			}
		}
	}

	public void draw_signal(BufferedImage image)
	{
		DataBuffer imageBuf = image.getRaster().getDataBuffer();
		int pi = 0;
		for (int i = 0; i < this.signal.length; ++i)
		{
			final int ire = (int)Math.rint(this.signal[i]) - AppleNTSC.SYNC_LEVEL;
			final int val = (int)Math.rint(ire * 255.0 / (AppleNTSC.WHITE_LEVEL - AppleNTSC.SYNC_LEVEL));
			final int rgb = (val << 16) | (val << 8) | (val);
			imageBuf.setElem(pi,rgb);
			++pi;
			if (pi % AppleNTSC.H == 0)
			{
				pi += AppleNTSC.H;
			}
		}
	}

	public void analogtv_setup_sync()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			int i = AppleNTSC.SYNC_START;
			if (lineno < 70)
			{
				while (i < AppleNTSC.BP_START)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.BLANK_LEVEL;
				}
				while (i < AppleNTSC.H)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.SYNC_LEVEL;
				}
			}
			else
			{
				while (i < AppleNTSC.BP_START)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.SYNC_LEVEL;
				}
				while (i < AppleNTSC.PIC_START)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.BLANK_LEVEL;
				}
				while (i < AppleNTSC.FP_START)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.BLACK_LEVEL;
				}
				while (i < AppleNTSC.H)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.BLANK_LEVEL;
				}
				for (int icb = AppleNTSC.CB_START; icb < AppleNTSC.CB_END; icb += 4)
				{
					this.signal[lineno * AppleNTSC.H + icb + 1] = AppleNTSC.CB_LEVEL;
					this.signal[lineno * AppleNTSC.H + icb + 3] = -AppleNTSC.CB_LEVEL;
				}
				// only for Rev. 0 boards:
				this.signal[lineno * AppleNTSC.H + AppleNTSC.SPIKE] = -AppleNTSC.CB_LEVEL;
			}
			if (lineno != 70 && lineno != 261)
				continue;
			// TEST:
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 1] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 2] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 3] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 300] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 301] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 302] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 303] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 304] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 305] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 306] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 307] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 308] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 309] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 310] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 311] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 49] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 58] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 67] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 76] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 86] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 87] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 95] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 96] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 104] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 105] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 113] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 114] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 122] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 123] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 131] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 132] = -20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 500] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 502] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 504] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 506] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 558] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 559] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 560] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 561] = 80;
		}
	}

	public void analogtv_read_color_info()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			final int isp = lineno * AppleNTSC.H;//+ (cur_hsync & ~3);
			//				// dump signal:
			//				System.out.print("lineno "+lineno+" cb_phase:");
			//				for (int i = AppleNTSC.CB_START + 8; i < AppleNTSC.CB_START + 36 - 8; ++i)
			//				{
			//					System.out.printf(" %+6.2f",(this.signal[isp + i] * this.agclevel * cbfc));
			//				}
			//				System.out.println();
			//					System.out.print("lineno "+lineno+" cb_phase:");
			for (int i = AppleNTSC.CB_START + 8; i < AppleNTSC.CB_END - 8; ++i)
			{
				this.cb_phase[i & 3] = this.signal[isp + i] * this.agclevel;
				//					System.out.printf(" %+6.2f",this.cb_phase[i & 3]);
			}
			double tot = .1;
			for (int i = 0; i < 4; ++i)
			{
				tot += this.cb_phase[i] * this.cb_phase[i];
			}
			final double cbgain = 32.0 / Math.sqrt(tot);
			//				System.out.print("lineno "+lineno+" cb_phase:");
			for (int i = 0; i < 4; i++)
			{
				this.line_cb_phase[lineno][i] = this.cb_phase[i] * cbgain;
				//					System.out.print(" "+this.cb_phase[i]);
			}
			//				System.out.println();
		}
		//	 		System.out.println("synch (v,h): "+cur_vsync+","+cur_hsync);
	}

	private static final int MAXDELAY = 32;

	private void analogtv_ntsc_to_yiq(int lineno, int isignal, int start, int end)
	{
		int i;
		final int phasecorr = 0; // ???
		boolean colormode;
		double brightadd = AppleNTSC.BLACK_LEVEL;/*-AppleNTSC.SYNC_LEVEL+1;*///this.brightness_control * 100.0 - AppleNTSC.BLACK_LEVEL;
		double[] delay = new double[2 * MAXDELAY + AppleNTSC.H];
		double[] multiq2 = new double[4];
		double cb_i = (this.line_cb_phase[lineno][(2 + phasecorr) & 3] - this.line_cb_phase[lineno][(0 + phasecorr) & 3]) / 16.0;
		double cb_q = (this.line_cb_phase[lineno][(3 + phasecorr) & 3] - this.line_cb_phase[lineno][(1 + phasecorr) & 3]) / 16.0;
		if (lineno == 8 || lineno == 100)
			System.out.println("" + lineno + " " + cb_i + "," + cb_q);
		colormode = (cb_i * cb_i + cb_q * cb_q) > 2.8;
		//			System.out.println(""+lineno+" "+colormode);
		if (colormode)
		{
			double tint_i = -Math.cos((103 + this.color_control) * Math.PI / 180);
			double tint_q = Math.sin((103 + this.color_control) * Math.PI / 180);
			multiq2[0] = (cb_i * tint_i - cb_q * tint_q) * this.color_control;
			multiq2[1] = (cb_q * tint_i + cb_i * tint_q) * this.color_control;
			multiq2[2] = -multiq2[0];
			multiq2[3] = -multiq2[1];
			//				System.out.println("multi: "+multiq2[0]+","+multiq2[1]+","+multiq2[2]+","+multiq2[3]);
		}
		int idp = AppleNTSC.H + MAXDELAY;
		for (i = 0; i < 24; i++)
		{
			delay[idp + i] = 0.0;
		}
		int iyiq;
		int isp;
		for (i = start, iyiq = start, isp = start; i < end; i++, idp--, iyiq++, isp++)
		{
			delay[idp + 0] = this.signal[isignal + isp + 0] * 0.0469904257251935 * this.agclevel;
			delay[idp + 8] = (+1.0 * (delay[idp + 6] + delay[idp + 0]) + 4.0 * (delay[idp + 5] + delay[idp + 1]) + 7.0 * (delay[idp + 4] + delay[idp + 2]) + 8.0 * (delay[idp + 3]) - 0.0176648
				* delay[idp + 12] - 0.4860288 * delay[idp + 10]);
			this.yiq[iyiq].y = delay[idp + 8] + brightadd;
			//				System.out.printf("%6.1f  ",this.yiq[iyiq].y);
		}
		//			System.out.println();
		if (colormode)
		{
			idp = AppleNTSC.H + MAXDELAY;
			for (i = 0; i < 27; i++)
			{
				delay[idp + i] = 0.0;
			}
			for (i = start, iyiq = start, isp = start; i < end; i++, idp--, iyiq++, isp++)
			{
				// const double sig(*sp);
				delay[idp + 0] = this.signal[isignal + isp] * multiq2[i & 3] * 0.0833333333333;
				this.yiq[iyiq].i = delay[idp + 8] = (+1.0 * (delay[idp + 5] + delay[idp + 0]) + 3.0 * (delay[idp + 4] + delay[idp + 1]) + 4.0 * (delay[idp + 3] + delay[idp + 2]) - 0.3333333333 * delay[idp + 10]);
				delay[idp + 16] = this.signal[isignal + isp] * multiq2[(i + 3) & 3] * 0.0833333333333;
				this.yiq[iyiq].q = delay[idp + 24] = (+1.0 * (delay[idp + 16 + 5] + delay[idp + 16 + 0]) + 3.0 * (delay[idp + 16 + 4] + delay[idp + 16 + 1]) + 4.0
					* (delay[idp + 16 + 3] + delay[idp + 16 + 2]) - 0.3333333333 * delay[idp + 24 + 2]);
			}
			//				System.out.println();
		}
		else
		{
			for (i = start, iyiq = start; i < end; i++, iyiq++)
			{
				this.yiq[iyiq].i = this.yiq[iyiq].q = 0.0;
			}
		}
	}

	public void analogtv_test_draw(final BufferedImage image)
	{
		final DataBuffer imageBuf = image.getRaster().getDataBuffer();
		int pi = 0;
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			final int isignal = lineno * AppleNTSC.H;//((lineno + this.cur_vsync + AppleNTSC.V) % AppleNTSC.V) * AppleNTSC.H + this.line_hsync[lineno];
			analogtv_ntsc_to_yiq(lineno,isignal,0,AppleNTSC.H);
			for (int colno = 0; colno < AppleNTSC.H; ++colno)
			{
				final analogtv_yiq yiql = this.yiq[colno];
				if (yiql.y < 0)
					yiql.y = 0;
				double r = yiql.y + 0.948 * yiql.i + 0.624 * yiql.q;
				double g = yiql.y - 0.276 * yiql.i - 0.639 * yiql.q;
				double b = yiql.y - 1.105 * yiql.i + 1.729 * yiql.q;
				//					System.out.printf("(%6.1f,%6.1f,%6.1f) ",r,g,b);
				final int ir = (int)Math.rint(r * 255 / 140);
				final int ig = (int)Math.rint(g * 255 / 140);
				final int ib = (int)Math.rint(b * 255 / 140);
				final int rgb = ((ir << 16) | (ig << 8) | (ib)) & 0xFFFFFF;
				imageBuf.setElem(pi,rgb);
				imageBuf.setElem(pi + AppleNTSC.H,rgb);
				++pi;
				if (pi % AppleNTSC.H == 0)
				{
					pi += AppleNTSC.H;
				}
			}
			//				System.out.println();
		}
	}

}
