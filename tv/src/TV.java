import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;

/*
 * Created on Jan 18, 2008
 */
public class TV implements Closeable
{
	private static TV thistv;
	public static void main(final String... args) throws InterruptedException, InvocationTargetException
	{
		Thread.currentThread().setName("User-main");
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				thistv = new TV();
				program();
			}
		});
		synchronized (thistv.shutdown)
		{
			while (!thistv.shutdown.get())
			{
				try
				{
					thistv.shutdown.wait();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static void program()
	{
		System.out.println("SYNC_START: "+ANALOGTV.SYNC_START);
		System.out.println("  BP_START: "+ANALOGTV.BP_START);
		System.out.println("  CB_START: "+ANALOGTV.CB_START);
		System.out.println(" PIC_START: "+ANALOGTV.PIC_START);
		System.out.println("  FP_START: "+ANALOGTV.FP_START);

		BufferedImage image = new BufferedImage(ANALOGTV.H,ANALOGTV.V * 2,BufferedImage.TYPE_INT_RGB);
		GUI gui = new GUI(thistv,image);

		analogtv_ tv = new analogtv_();

		analogtv_setup_sync(tv);
		analogtv_read_color_info(tv);

//		dump_signal(in.signal);
//		draw_signal(in.signal,image);

		analogtv_test_draw(tv,image);
	}

	private static void dump_signal(byte[] signal)
	{
		int pi = 0;
		for (int i = 0; i < signal.length; ++i)
		{
			System.out.printf(" %+4d",signal[i]);
			++pi;
			if (pi >= ANALOGTV.H)
			{
				System.out.println();
				pi  = 0;
			}
		}
	}

	private static void draw_signal(double[] signal, BufferedImage image)
	{
		DataBuffer imageBuf = image.getRaster().getDataBuffer();
		int pi = 0;
		for (int i = 0; i < signal.length; ++i)
		{
			final int ire = (int)Math.rint(signal[i])-ANALOGTV.SYNC_LEVEL;
			final int val = (int)Math.rint(ire*255.0/(ANALOGTV.WHITE_LEVEL-ANALOGTV.SYNC_LEVEL));
			final int rgb = (val << 16) | (val << 8) | (val);
			imageBuf.setElem(pi,rgb);
			++pi;
			if (pi % ANALOGTV.H == 0)
			{
				pi += ANALOGTV.H;
			}
		}
	}

	private static void analogtv_setup_sync(analogtv_ input)
	{
		for (int lineno = 0; lineno < ANALOGTV.V; ++lineno)
		{
			int i = ANALOGTV.SYNC_START;
			if (lineno < 70)
			{
				while (i < ANALOGTV.BP_START)
				{
					input.signal[lineno * ANALOGTV.H + i++] = ANALOGTV.BLANK_LEVEL;
				}
				while (i < ANALOGTV.H)
				{
					input.signal[lineno * ANALOGTV.H + i++] = ANALOGTV.SYNC_LEVEL;
				}
			}
			else
			{
				while (i < ANALOGTV.BP_START)
				{
					input.signal[lineno * ANALOGTV.H + i++] = ANALOGTV.SYNC_LEVEL;
				}
				while (i < ANALOGTV.PIC_START)
				{
					input.signal[lineno * ANALOGTV.H + i++] = ANALOGTV.BLANK_LEVEL;
				}
				while (i < ANALOGTV.FP_START)
				{
					input.signal[lineno * ANALOGTV.H + i++] = ANALOGTV.BLACK_LEVEL;
				}
				while (i < ANALOGTV.H)
				{
					input.signal[lineno * ANALOGTV.H + i++] = ANALOGTV.BLANK_LEVEL;
				}
				for (int icb = ANALOGTV.CB_START; icb < ANALOGTV.CB_END; icb += 4)
				{
					input.signal[lineno * ANALOGTV.H + icb + 1] = ANALOGTV.CB_LEVEL;
					input.signal[lineno * ANALOGTV.H + icb + 3] = -ANALOGTV.CB_LEVEL;
				}
				// only for Rev. 0 boards:
				input.signal[lineno * ANALOGTV.H + ANALOGTV.SPIKE] = -ANALOGTV.CB_LEVEL;
			}
			if (lineno != 70 && lineno != 261) continue;
			// TEST:
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START] = 80;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+1] = 80;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+2] = 80;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+3] = 80;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 300] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 301] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 302] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 303] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 304] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 305] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 306] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 307] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 308] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 309] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 310] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 311] = 100;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 49] = 80;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 58] = 80;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 67] = 80;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 76] = 80;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 86] = 40;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 87] = 40;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 95] = 40;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 96] = 40;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 104] = 40;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 105] = 40;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 113] = 40;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 114] = 40;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 122] = 40;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 123] = 40;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 131] = 20;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 132] = -20;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 500] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 502] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 504] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 506] = 100;

			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 558] = 80;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 559] = 80;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 560] = 80;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 561] = 80;
		}
	}

	private static void analogtv_read_color_info(analogtv_ it)
	{
		for (int lineno = 0; lineno < ANALOGTV.V; ++lineno)
		{
			final int isp = lineno * ANALOGTV.H ;//+ (cur_hsync & ~3);

//			// dump signal:
//			System.out.print("lineno "+lineno+" cb_phase:");
//			for (int i = ANALOGTV.CB_START + 8; i < ANALOGTV.CB_START + 36 - 8; ++i)
//			{
//				System.out.printf(" %+6.2f",(it.signal[isp + i] * it.agclevel * cbfc));
//			}
//			System.out.println();

//				System.out.print("lineno "+lineno+" cb_phase:");
			for (int i = ANALOGTV.CB_START + 8; i < ANALOGTV.CB_END - 8; ++i)
			{
				it.cb_phase[i & 3] = it.signal[isp + i] * it.agclevel;
//				System.out.printf(" %+6.2f",it.cb_phase[i & 3]);
			}

			double tot = .1;
			for (int i = 0; i < 4; ++i)
			{
				tot += it.cb_phase[i] * it.cb_phase[i];
			}
			final double cbgain = 32.0 / Math.sqrt(tot);
//			System.out.print("lineno "+lineno+" cb_phase:");
			for (int i = 0; i < 4; i++)
			{
				it.line_cb_phase[lineno][i] = it.cb_phase[i] * cbgain;
//				System.out.print(" "+it.cb_phase[i]);
			}
//			System.out.println();
		}
// 		System.out.println("synch (v,h): "+cur_vsync+","+cur_hsync);
	}

	private static final int MAXDELAY = 32;

	private static void analogtv_ntsc_to_yiq(analogtv_ it, int lineno, int isignal, int start, int end)
	{
		int i;
		final int phasecorr = 0; // ???
		boolean colormode;
		double agclevel = it.agclevel;
		double brightadd = ANALOGTV.BLACK_LEVEL;/*-ANALOGTV.SYNC_LEVEL+1;*///it.brightness_control * 100.0 - ANALOGTV.BLACK_LEVEL;
		double[] delay = new double[2*MAXDELAY + ANALOGTV.H];
		double[] multiq2 = new double[4];
		double cb_i = (it.line_cb_phase[lineno][(2 + phasecorr) & 3] - it.line_cb_phase[lineno][(0 + phasecorr) & 3]) / 16.0;
		double cb_q = (it.line_cb_phase[lineno][(3 + phasecorr) & 3] - it.line_cb_phase[lineno][(1 + phasecorr) & 3]) / 16.0;
		if (lineno == 8 || lineno == 100)
		System.out.println(""+lineno+" "+cb_i+","+cb_q);
		colormode = (cb_i * cb_i + cb_q * cb_q) > 2.8;
//		System.out.println(""+lineno+" "+colormode);
		if (colormode)
		{
			double tint_i = -Math.cos((103 + it.color_control) * Math.PI / 180);
			double tint_q = Math.sin((103 + it.color_control) * Math.PI / 180);
			multiq2[0] = (cb_i * tint_i - cb_q * tint_q) * it.color_control;
			multiq2[1] = (cb_q * tint_i + cb_i * tint_q) * it.color_control;
			multiq2[2] = -multiq2[0];
			multiq2[3] = -multiq2[1];
//			System.out.println("multi: "+multiq2[0]+","+multiq2[1]+","+multiq2[2]+","+multiq2[3]);
		}
		int idp = ANALOGTV.H + MAXDELAY;
		for (i = 0; i < 24; i++)
		{
			delay[idp + i] = 0.0;
		}
		int iyiq;
		int isp;
		for (i = start, iyiq = start, isp = start; i < end; i++, idp--, iyiq++, isp++)
		{
			delay[idp + 0] = it.signal[isignal + isp + 0] * 0.0469904257251935 * agclevel;
			delay[idp + 8] = (
				+ 1.0 * (delay[idp + 6] + delay[idp + 0])
				+ 4.0 * (delay[idp + 5] + delay[idp + 1])
				+ 7.0 * (delay[idp + 4] + delay[idp + 2])
				+ 8.0 * (delay[idp + 3])
				- 0.0176648 * delay[idp + 12]
				- 0.4860288 * delay[idp + 10]);
			it.yiq[iyiq].y = delay[idp + 8] + brightadd;
//			System.out.printf("%6.1f  ",it.yiq[iyiq].y);
		}
//		System.out.println();
		if (colormode)
		{
			idp = ANALOGTV.H + MAXDELAY;
			for (i = 0; i < 27; i++)
			{
				delay[idp + i] = 0.0;
			}
			for (i = start, iyiq = start, isp = start; i < end; i++, idp--, iyiq++, isp++)
			{
				// const double sig(*sp);
				delay[idp + 0] = it.signal[isignal + isp] * multiq2[i & 3] * 0.0833333333333;
				it.yiq[iyiq].i = delay[idp + 8] = (
					+ 1.0 * (delay[idp + 5] + delay[idp + 0])
					+ 3.0 * (delay[idp + 4] + delay[idp + 1])
					+ 4.0 * (delay[idp + 3] + delay[idp + 2])
					- 0.3333333333 * delay[idp + 10]);
				delay[idp + 16] = it.signal[isignal + isp] * multiq2[(i + 3) & 3] * 0.0833333333333;
				it.yiq[iyiq].q = delay[idp + 24] = (
					+ 1.0 * (delay[idp + 16 + 5] + delay[idp + 16 + 0])
					+ 3.0 * (delay[idp + 16 + 4] + delay[idp + 16 + 1])
					+ 4.0 * (delay[idp + 16 + 3] + delay[idp + 16 + 2])
					- 0.3333333333 * delay[idp + 24 + 2]);
			}
//			System.out.println();
		}
		else
		{
			for (i = start, iyiq = start; i < end; i++, iyiq++)
			{
				it.yiq[iyiq].i = it.yiq[iyiq].q = 0.0;
			}
		}
	}

	private static void analogtv_test_draw(analogtv_ it, final BufferedImage image)
	{
		final DataBuffer imageBuf = image.getRaster().getDataBuffer();
		int pi = 0;
		for (int lineno = 0; lineno < ANALOGTV.V; ++lineno)
		{
			final int isignal = lineno * ANALOGTV.H;//((lineno + it.cur_vsync + ANALOGTV.V) % ANALOGTV.V) * ANALOGTV.H + it.line_hsync[lineno];
			analogtv_ntsc_to_yiq(it,lineno,isignal,0,ANALOGTV.H);
			for (int colno = 0; colno < ANALOGTV.H; ++colno)
			{
				final analogtv_yiq yiq = it.yiq[colno];
				if (yiq.y < 0) yiq.y = 0;
				double r = yiq.y + 0.948 * yiq.i + 0.624 * yiq.q;
				double g = yiq.y - 0.276 * yiq.i - 0.639 * yiq.q;
				double b = yiq.y - 1.105 * yiq.i + 1.729 * yiq.q;
//				System.out.printf("(%6.1f,%6.1f,%6.1f) ",r,g,b);
				final int ir = (int)Math.rint(r*255/140);
				final int ig = (int)Math.rint(g*255/140);
				final int ib = (int)Math.rint(b*255/140);
				final int rgb = ((ir << 16) | (ig << 8) | (ib)) & 0xFFFFFF;
				imageBuf.setElem(pi,rgb);
				imageBuf.setElem(pi+ANALOGTV.H,rgb);
				++pi;
				if (pi % ANALOGTV.H == 0)
				{
					pi += ANALOGTV.H;
				}
			}
//			System.out.println();
		}
	}

	private final AtomicBoolean shutdown = new AtomicBoolean();
	public void close()
	{
		synchronized (this.shutdown)
		{
			this.shutdown.set(true);
			this.shutdown.notifyAll();
		}
	}
}
