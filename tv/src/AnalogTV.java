import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/*
 * Created on Jan 18, 2008
 */
public class AnalogTV
{
	private double[] signal = new double[AppleNTSC.SIGNAL_LEN];

	private double agclevel = 1;
	private double color_control = 1;



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

	public void write_sync_signal()
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
//				for (int icb = AppleNTSC.CB_START; icb < AppleNTSC.CB_END; icb += 4)
//				{
//					this.signal[lineno * AppleNTSC.H + icb + 1] = AppleNTSC.CB_LEVEL;
//					this.signal[lineno * AppleNTSC.H + icb + 3] = -AppleNTSC.CB_LEVEL;
//				}
				for (int icb = AppleNTSC.CB_START; icb < AppleNTSC.CB_END; icb += 4)
				{
					this.signal[lineno * AppleNTSC.H + icb + 1] = AppleNTSC.CB_LEVEL/2;
					this.signal[lineno * AppleNTSC.H + icb + 2] = AppleNTSC.CB_LEVEL/2;
					this.signal[lineno * AppleNTSC.H + icb + 3] = -AppleNTSC.CB_LEVEL/2;
					this.signal[lineno * AppleNTSC.H + icb + 4] = -AppleNTSC.CB_LEVEL/2;
				}
				// only for Rev. 0 boards:
				this.signal[lineno * AppleNTSC.H + AppleNTSC.SPIKE] = -AppleNTSC.CB_LEVEL;
			}
		}
	}

	public void write_play_signal1()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 1] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 2] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 3] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 49] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 58] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 67] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 76] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 86] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 87] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 95] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 96] = 40;



			// purple
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 104] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 105] = 40;

			// blue
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 113] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 114] = 40;

			// green
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 122] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 123] = 40;

			// ???
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 131] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 132] = 40;




			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 140] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 142] = -20;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 149] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 151] = -20;
			// orange
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 158] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 160] = -20;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 167] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 169] = -20;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 558] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 559] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 560] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 561] = 80;
		}
	}

	public void write_play_signal2()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 000] = 0;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 015] = 0;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 030] = 0;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 045] = 0;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 100] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 115] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 130] = 20;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 145] = 20;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 200] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 215] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 230] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 245] = 40;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 300] = 60;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 315] = 60;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 330] = 60;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 345] = 60;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 400] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 415] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 430] = 80;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 445] = 80;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 500] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 515] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 530] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 545] = 100;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 600] = 120;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 615] = 120;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 630] = 120;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 645] = 120;
		}
	}

	public void write_play_signal3()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 100] = 10;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 101] = 10;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 115] = 10;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 116] = 10;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 130] = 10;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 131] = 10;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 145] = 10;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 146] = 10;



			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 200] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 201] = 25;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 215] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 216] = 25;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 230] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 231] = 25;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 245] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 246] = 25;


			
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 300] = 30;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 301] = 30;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 315] = 30;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 316] = 30;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 330] = 30;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 331] = 30;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 345] = 30;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 346] = 30;


			
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 400] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 401] = 40;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 415] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 416] = 40;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 430] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 431] = 40;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 445] = 40;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 446] = 40;


			
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 500] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 501] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 515] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 516] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 530] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 531] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 545] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 546] = 50;
		}
	}

	public void write_play_signal4()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 100] = 100;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 201] = 100;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 302] = 100;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 403] = 100;



			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 152] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 153] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 253] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 254] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 354] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 355] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 455] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 456] = 50;
		}
	}

	public void write_apple_color_test()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			int i = AppleNTSC.PIC_START;
			for (int bi = 0; bi < 35; bi += 4)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 120;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 80;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
			}
			for (int bi = 0; bi < 35; bi += 4)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 120;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 80;
			}
			for (int bi = 0; bi < 35; bi += 4)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 80;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 120;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
			}
			for (int bi = 0; bi < 35; bi += 4)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 80;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 120;
			}
		}
	}
	
	public void write_modified_color_bars()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			int i = AppleNTSC.PIC_START;
			// white
			for (int bi = 0; bi < 35; ++bi)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 100;
			}
			// yellow
			for (int bi = 0; bi < 35; bi += 4)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 131;
				this.signal[lineno * AppleNTSC.H + i++] = 48;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
			}
			// cyan
			for (int bi = 0; bi < 35; bi += 4)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 13;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 131;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
			}
			// green
			for (int bi = 0; bi < 35; bi += 4)
			{
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 7;
				this.signal[lineno * AppleNTSC.H + i++] = 0;
				this.signal[lineno * AppleNTSC.H + i++] = 116;
			}
		}
	}
	
	public double[] get_cb_phase(int lineno)
	{
		final double[] phase = new double[4];
		final int isp = lineno * AppleNTSC.H;
		for (int i = AppleNTSC.CB_START + 8; i < AppleNTSC.CB_END - 8; ++i)
		{
			phase[i & 3] = this.signal[isp + i] * this.agclevel;
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

	private static final int MAXDELAY = 32;
	private static final double COLOR_THRESH = 2.8;
	private static final double COLOR_BASE = 103;
	private static final double CHROMA_FACTOR = 1/12.0;
	private static final double LUMA_FACTOR = 0.0469904257251935; // ??? where does this come from?

	private analogtv_yiq[] ntsc_to_yiq(final int lineno, final int isignal, final int start, final int end, final double[] cb_phase)
	{
		final double cb_i = (cb_phase[2] - cb_phase[0]) / 16;
		final double cb_q = (cb_phase[3] - cb_phase[1]) / 16;
		final boolean colormode = (COLOR_THRESH <= cb_i*cb_i + cb_q*cb_q);

		final double[] iq_factor = new double[4];
		if (colormode)
		{
			final double tint_i = -Math.cos((COLOR_BASE + this.color_control) * Math.PI / 180);
			final double tint_q = +Math.sin((COLOR_BASE + this.color_control) * Math.PI / 180);
			iq_factor[0] = (cb_i * tint_i - cb_q * tint_q) * this.color_control;
			iq_factor[2] = -iq_factor[0];
			iq_factor[1] = (cb_q * tint_i + cb_i * tint_q) * this.color_control;
			iq_factor[3] = -iq_factor[1];
		}

		final analogtv_yiq[] yiq = new analogtv_yiq[AppleNTSC.H];

		// calculate luma (Y)
		{
			final double[] delay = new double[2 * MAXDELAY + AppleNTSC.H];
			int idp = AppleNTSC.H + MAXDELAY;
			for (int i = start; i < end; ++i, --idp)
			{
				delay[idp + 0] = this.signal[isignal + i] * LUMA_FACTOR * this.agclevel;
				delay[idp + 8] = (+1.0 * (delay[idp + 6] + delay[idp + 0]) + 4.0 * (delay[idp + 5] + delay[idp + 1]) + 7.0 * (delay[idp + 4] + delay[idp + 2]) + 8.0 * (delay[idp + 3]) - 0.0176648 * delay[idp + 12] - 0.4860288 * delay[idp + 10]);
				yiq[i] = new analogtv_yiq();
				yiq[i].y = delay[idp + 8] + AppleNTSC.BLACK_LEVEL;
			}
		}

		// calculate chroma (I, Q)
		if (colormode)
		{
			final double[] delay = new double[2 * MAXDELAY + AppleNTSC.H];
			int idp = AppleNTSC.H + MAXDELAY;
			for (int i = start; i < end; ++i, --idp)
			{
				delay[idp + 00] = this.signal[isignal + i] * iq_factor[(i + 0) & 3] * CHROMA_FACTOR;
				yiq[i].i = delay[idp + 8] = (+1.0 * (delay[idp + 5] + delay[idp + 0]) + 3.0 * (delay[idp + 4] + delay[idp + 1]) + 4.0 * (delay[idp + 3] + delay[idp + 2]) - 0.3333333333 * delay[idp + 10]);

				delay[idp + 16] = this.signal[isignal + i] * iq_factor[(i + 3) & 3] * CHROMA_FACTOR;
				yiq[i].q = delay[idp + 24] = (+1.0 * (delay[idp + 16 + 5] + delay[idp + 16 + 0]) + 3.0 * (delay[idp + 16 + 4] + delay[idp + 16 + 1]) + 4.0 * (delay[idp + 16 + 3] + delay[idp + 16 + 2]) - 0.3333333333 * delay[idp + 24 + 2]);
			}
		}
		else // black & white
		{
			for (int i = start; i < end; ++i)
			{
				yiq[i].i = yiq[i].q = 0.0;
			}
		}

		return yiq;
	}

	public void test_draw(final BufferedImage image)
	{
		final DataBuffer imageBuf = image.getRaster().getDataBuffer();

		int pi = 0;
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			final double[] cb_phase = get_cb_phase(lineno);
			final analogtv_yiq[] yiq = ntsc_to_yiq(lineno,lineno * AppleNTSC.H,0,AppleNTSC.H,cb_phase);
			for (int colno = 0; colno < AppleNTSC.H; ++colno)
			{
				final int rgb = yiq2rgb(yiq[colno]);
				imageBuf.setElem(pi,rgb);
				imageBuf.setElem(pi + AppleNTSC.H,rgb);

				++pi;
				if (pi % AppleNTSC.H == 0)
				{
					pi += AppleNTSC.H;
				}
			}
		}
	}

	private static final double FACTOR_RED   = 256.0/140.0;
	private static final double FACTOR_GREEN = 256.0/140.0;
	private static final double FACTOR_BLUE  = 256.0/140.0;

	private static int yiq2rgb(final analogtv_yiq yiq)
	{
		if (yiq.y < 0)
			yiq.y = 0;

		final double r = yiq.y + 0.956 * yiq.i + 0.621 * yiq.q;
		final double g = yiq.y - 0.272 * yiq.i - 0.647 * yiq.q;
		final double b = yiq.y - 1.105 * yiq.i + 1.702 * yiq.q;

		int ir = (int)Math.rint(r * FACTOR_RED);
		ir = clamp(0,ir,256);
		int ig = (int)Math.rint(g * FACTOR_GREEN);
		ig = clamp(0,ig,256);
		int ib = (int)Math.rint(b * FACTOR_BLUE);
		ib = clamp(0,ib,256);

		return
			((ir & 0xFF) << 16)| 
			((ig & 0xFF) <<  8)| 
			((ib & 0xFF) <<  0);
	}

	private static int clamp(int min, int x, int lim)
	{
		if (x < min)
			return min;
		if (lim <= x)
			return lim-1;
		return x;
	}
}
