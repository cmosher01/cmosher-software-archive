import java.awt.Color;
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
		BufferedImage image = new BufferedImage(ANALOGTV.H,ANALOGTV.V * 2,BufferedImage.TYPE_INT_RGB);
		GUI gui = new GUI(thistv,image);

		analogtv_input tvin = new analogtv_input();
		analogtv_setup_sync(tvin);

//		draw_signal(tvin.signal,image);

		analogtv_ tv = new analogtv_();
		analogtv_configure(tv);
		analogtv_reception rcp = new analogtv_reception();
		rcp.input = tvin;
		analogtv_add_signal(tv,rcp);

//		draw_signal(tv.rx_signal,image);

		analogtv_test_draw(tv,image);
//		analogtv_draw(tv,image);

		//gui.plot();

	}

	private static void draw_signal(double[] rx_signal, BufferedImage image)
	{
		byte[] rb = new byte[rx_signal.length];
		int i = 0;
		for (final double sig: rx_signal)
		{
			rb[i++] = (byte)(int)Math.rint((sig*255/480));
		}
		draw_signal(rb,image);
	}

	private static void draw_signal(byte[] signal, BufferedImage image)
	{
		DataBuffer imageBuf = image.getRaster().getDataBuffer();
		int pi = 0;
		for (int i = 0; i < signal.length; ++i)
		{
			final int ire = signal[i]-ANALOGTV.SYNC_LEVEL;
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

	private static double puramp(analogtv_ it, double tc, double start, double over)
	{
		final double pt = it.powerup - start;
		if (pt < 0.0)
			return 0.0;
		if (pt > 900.0 || pt / tc > 8.0)
			return 1.0;
		final double ret = (1.0 - Math.exp(-pt / tc)) * over;
		if (ret > 1.0)
			return 1.0;
		return ret * ret;
	}

	private static void analogtv_setup_sync(analogtv_input input)
	{
		for (int lineno = 0; lineno < ANALOGTV.V; ++lineno)
		{
			{
				int i = ANALOGTV.SYNC_START;
				if (3 <= lineno && lineno < 7)
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
				}
				while (i < ANALOGTV.H)
				{
					input.signal[lineno * ANALOGTV.H + i++] = ANALOGTV.BLANK_LEVEL;
				}
			}
			/* 9 cycles of colorburst */
			for (int i = ANALOGTV.CB_START; i < ANALOGTV.CB_START + 36; i += 4)
			{
				input.signal[lineno * ANALOGTV.H + i + 1] += ANALOGTV.CB_LEVEL;
				input.signal[lineno * ANALOGTV.H + i + 3] -= ANALOGTV.CB_LEVEL;
			}

			// TEST:
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 300] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 301] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 302] = 100;
			input.signal[lineno * ANALOGTV.H + ANALOGTV.PIC_START+ 303] = 100;

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
		}
	}

	private static final double cbfc = 1.0 / 128.0;

	private static void analogtv_sync(analogtv_ it)
	{
		int i;
		int cur_vsync = it.cur_vsync;
		for (i = -32; i < 32; i++)
		{
			int xlineno = (cur_vsync + i + ANALOGTV.V) % ANALOGTV.V;
			double filt = 0.0;
			for (int j = 0; j < ANALOGTV.H; j += ANALOGTV.H / 16)
			{
				filt += it.rx_signal[xlineno * ANALOGTV.H + j];
			}
			filt *= it.agclevel;
			final double osc = (double)(ANALOGTV.V + i) / (double)ANALOGTV.V;
			if (osc >= 1.05 + 0.0002 * filt)
			{
				break;
			}
		}
		cur_vsync = (cur_vsync + i + ANALOGTV.V) % ANALOGTV.V;
		int cur_hsync = it.cur_hsync;
		int lineno;
		for (lineno = 0; lineno < ANALOGTV.V; lineno++)
		{
			if (lineno > 5 && lineno < ANALOGTV.V - 3)
			{
				/* ignore vsync interval */
				final int spi = ((lineno + cur_vsync + ANALOGTV.V) % ANALOGTV.V) * ANALOGTV.H + cur_hsync;
				for (i = -8; i < 8; i++)
				{
					final double osc = (double)(ANALOGTV.H + i) / (double)ANALOGTV.H;
					final double filt = (it.rx_signal[spi + i - 3] + it.rx_signal[spi + i - 2] + it.rx_signal[spi + i - 1] + it.rx_signal[spi + i]) * it.agclevel;
					if (osc >= 1.005 + 0.0001 * filt)
					{
						break;
					}
				}
				cur_hsync = (cur_hsync + i + ANALOGTV.H) % ANALOGTV.H;
			}
			it.line_hsync[lineno] = (cur_hsync + ANALOGTV.PIC_START + ANALOGTV.H) % ANALOGTV.H;
			if (lineno > 15)
			{
				final int isp = lineno * ANALOGTV.H + (cur_hsync & ~3);
				for (i = ANALOGTV.CB_START + 8; i < ANALOGTV.CB_START + 36 - 8; ++i)
				{
					it.cb_phase[i & 3] = it.cb_phase[i & 3] * (1.0 - cbfc) + it.rx_signal[isp + i] * it.agclevel * cbfc;
				}
			}
			double tot = 0.1;
			double cbgain;
			for (i = 0; i < 4; i++)
			{
				tot += it.cb_phase[i] * it.cb_phase[i];
			}
			cbgain = 32.0 / Math.sqrt(tot);
			for (i = 0; i < 4; i++)
			{
				it.line_cb_phase[lineno][i] = it.cb_phase[i] * cbgain;
			}
		}
		it.cur_hsync = cur_hsync;
		it.cur_vsync = cur_vsync;
		// System.out.println("synch (v,h): "+cur_vsync+","+cur_hsync);
	}

	private static final int MAXDELAY = 32;

	private static int xxx;
	private static void analogtv_ntsc_to_yiq(analogtv_ it, int lineno, int isignal, int start, int end)
	{
		//System.out.println(""+xxx+" ntsc_to_yiq: "+lineno+","+isignal+","+start+","+end);
		++xxx;
		int i;
		// double *sp;
		final int phasecorr = 0;// ????????(signal-it.rx_signal)&3;
		boolean colormode;
		double agclevel = it.agclevel;
		double brightadd = ANALOGTV.BLACK_LEVEL;/*-ANALOGTV.SYNC_LEVEL+1;*///it.brightness_control * 100.0 - ANALOGTV.BLACK_LEVEL;
		double[] delay = new double[2*MAXDELAY + ANALOGTV.H];
		// double *dp;
		double[] multiq2 = new double[4];
		double cb_i = (it.line_cb_phase[lineno][(2 + phasecorr) & 3] - it.line_cb_phase[lineno][(0 + phasecorr) & 3]) / 16.0;
		double cb_q = (it.line_cb_phase[lineno][(3 + phasecorr) & 3] - it.line_cb_phase[lineno][(1 + phasecorr) & 3]) / 16.0;
		colormode = (cb_i * cb_i + cb_q * cb_q) > 2.8;
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
		// dp = delay+ANALOGTV.PIC_LEN-MAXDELAY;
		int idp = ANALOGTV.H + MAXDELAY;
		for (i = 0; i < 24; i++)
		{
			delay[idp + i] = 0.0;
		}
		int iyiq;
		int isp;
		for (i = start, iyiq = start, isp = start; i < end; i++, idp--, iyiq++, isp++)
		{
			if (idp < 0 || idp >= 2*MAXDELAY + ANALOGTV.H)
				System.err.println("err");
			delay[idp + 0] = it.rx_signal[isignal + isp + 0] * 0.0469904257251935 * agclevel;
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
		System.out.println();
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
				delay[idp + 0] = it.rx_signal[isignal + isp] * multiq2[i & 3] * 0.0833333333333;
				it.yiq[iyiq].i = delay[idp + 8] = (
					+ 1.0 * (delay[idp + 5] + delay[idp + 0])
					+ 3.0 * (delay[idp + 4] + delay[idp + 1])
					+ 4.0 * (delay[idp + 3] + delay[idp + 2])
					- 0.3333333333 * delay[idp + 10]);
				delay[idp + 16] = it.rx_signal[isignal + isp] * multiq2[(i + 3) & 3] * 0.0833333333333;
				it.yiq[iyiq].q = delay[idp + 24] = (
					+ 1.0 * (delay[idp + 16 + 5] + delay[idp + 16 + 0])
					+ 3.0 * (delay[idp + 16 + 4] + delay[idp + 16 + 1])
					+ 4.0 * (delay[idp + 16 + 3] + delay[idp + 16 + 2])
					- 0.3333333333 * delay[idp + 24 + 2]);
//				dumpiq(it.yiq[iyiq].i,it.yiq[iyiq].q);
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

	private static void dumpiq(double i, double q)
	{
		int ii = (int)Math.rint(i);
		int iq = (int)Math.rint(q);
		System.out.print(""+ii+" "+iq+"   ");
	}

	private static class levelclass
	{
		int index;
		double value;

		public levelclass()
		{
		}
	}

	private static final int MAX_LINEHEIGHT = 12;
	private static levelclass[][] leveltable = new levelclass[MAX_LINEHEIGHT + 1][MAX_LINEHEIGHT + 1];
	static
	{
		for (int i = 0; i < leveltable.length; ++i)
		{
			for (int j = 0; j < leveltable[i].length; ++j)
			{
				leveltable[i][j] = new levelclass();
			}
		}
	}

	private static void analogtv_configure(analogtv_ it)
	{
		/*
		 * If the window is very small, don't let the image we draw get lower
		 * than the actual TV resolution (266x200.)
		 * 
		 * If the aspect ratio of the window is within 20% of a 4:3 ratio, then
		 * scale the image to exactly fill the window.
		 * 
		 * Otherwise, center the image either horizontally or vertically,
		 * padding on the left+right, or top+bottom, but not both.
		 * 
		 * If it's very close (2.5%) to a multiple of VISLINES, make it exact
		 * For example, it maps 1024 => 1000.
		 */
		double percent = 0.20;
		double min_ratio = 4.0 / 3.0 * (1 - percent);
		double max_ratio = 4.0 / 3.0 * (1 + percent);
		double height_snap = 0.025;
		int hlim = ANALOGTV.V * 2;// ???it.xgwa.height;
		int wlim = ANALOGTV.H;// ???it.xgwa.width;
		final double ratio = wlim / (double)hlim;
		if (wlim < 266 || hlim < 200)
		{
			wlim = 266;
			hlim = 200;
		}
		else if (ratio > min_ratio && ratio < max_ratio)
		{
		}
		else if (ratio > max_ratio)
		{
			wlim = (int)Math.rint(Math.floor(hlim * max_ratio));
		}
		else
		/* ratio < min_ratio */
		{
			hlim = (int)Math.rint(Math.floor(wlim / min_ratio));
		}
		int height_diff = ((hlim + ANALOGTV.VISLINES / 2) % ANALOGTV.VISLINES) - ANALOGTV.VISLINES / 2;
		if (height_diff != 0 && Math.abs(height_diff) < hlim * height_snap)
		{
			hlim -= height_diff;
		}
		/* Most times this doesn't change */
		if (wlim != it.usewidth || hlim != it.useheight)
		{
			it.usewidth = wlim;
			it.useheight = hlim;
			it.xrepl = 1 + it.usewidth / 640;
			if (it.xrepl > 2)
				it.xrepl = 2;
			it.subwidth = it.usewidth / it.xrepl;
			// analogtv_free_image(it);
			// analogtv_alloc_image(it);
		}
		it.screen_xo = (ANALOGTV.V * 2/* ???it.xgwa.width */- it.usewidth) / 2;
		it.screen_yo = (ANALOGTV.H/* ???it.xgwa.height */- it.useheight) / 2;
		it.need_clear = 1;
	}

	private static double[] levelfac =
	{ -7.5, 5.5, 24.5 };

	private static void analogtv_setup_levels(analogtv_ it, double avgheight)
	{
//		System.out.println("setup_levels: "+avgheight);
		int i, height;
		for (height = 0; height < avgheight + 2.0 && height <= MAX_LINEHEIGHT; height++)
		{
			for (i = 0; i < height; i++)
			{
				leveltable[height][i].index = 2;
			}
			if (avgheight >= 3)
			{
				leveltable[height][0].index = 0;
			}
			if (avgheight >= 5)
			{
				leveltable[height][height - 1].index = 0;
			}
			if (avgheight >= 7)
			{
				leveltable[height][1].index = 1;
				leveltable[height][height - 2].index = 1;
			}
			for (i = 0; i < height; i++)
			{
				leveltable[height][i].value = (40.0 + levelfac[leveltable[height][i].index] * puramp(it,3.0,6.0,1.0)) / 256.0;
			}
		}
	}

	private static void analogtv_blast_imagerow(analogtv_ it, double[] rgb, int ytop, int ybot, final DataBuffer imageBuf)
	{
		// float *rpf;
		// char *level_copyfrom[3];
		// for (i=0; i<3; i++) level_copyfrom[i]=NULL;
		for (int y = ytop; y < ybot; y++)
		{
			// int level = leveltable[ybot - ytop][y - ytop].index;
			double levelmult = 1.0;// leveltable[ybot - ytop][y - ytop].value;
			// char *rowdata = 0;//it.image.data + y*it.image.bytes_per_line;
			// if (level_copyfrom[level])
			// {
			// memcpy(rowdata, level_copyfrom[level],
			// 65*14);//???it.image.bytes_per_line);
			// }
			// else
			// {
			// level_copyfrom[level] = rowdata;
			//System.out.println("y:" + y + ":   " + y*rgb.length/3+" to "+((y+1)*(rgb.length/3)));
			for (int x = 0, rpf = 0; rpf < rgb.length; x++, rpf += 3)
			{
				int ntscri = (int)Math.rint(Math.floor(rgb[rpf + 0] * levelmult));
				int ntscgi = (int)Math.rint(Math.floor(rgb[rpf + 1] * levelmult));
				int ntscbi = (int)Math.rint(Math.floor(rgb[rpf + 2] * levelmult));
				if (ntscri >= ANALOGTV.CV_MAX)
					ntscri = ANALOGTV.CV_MAX - 1;
				if (ntscgi >= ANALOGTV.CV_MAX)
					ntscgi = ANALOGTV.CV_MAX - 1;
				if (ntscbi >= ANALOGTV.CV_MAX)
					ntscbi = ANALOGTV.CV_MAX - 1;
				final int rgbval = (ntscri << 16) | (ntscgi << 8) | (ntscbi);
				if (y*(rgb.length/3)+x > ANALOGTV.SIGNAL_LEN) break;
				imageBuf.setElem(y*(rgb.length/3)+x,rgbval);
				// imageBuf.setElem(y*rgb.length+x+1,rgbval);
				// for (int j = 0; j < it.xrepl; j++) // 2 times
				// {
				// System.out.print("("+ntscri+","+ntscgi+","+ntscbi+")");
				// std::cout << "(" << ntscri << "," << ntscgi << "," <<
				// ntscbi << ") ";
				// XPutPixel(it.image, x*xrepl + j, y, it.red_values[ntscri]
				// | it.green_values[ntscgi] | it.blue_values[ntscbi]);
				// }
			}
			// }
		}
		// System.out.println();
		// std::cout << std::endl;
	}

	private static void analogtv_test_draw(analogtv_ it, final BufferedImage image)
	{
		final DataBuffer imageBuf = image.getRaster().getDataBuffer();
		analogtv_sync(it);
		analogtv_setup_levels(it,2.0);
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
				//imageBuf.setElem(pi+ANALOGTV.H,rgb);
				++pi;
				if (pi % ANALOGTV.H == 0)
				{
					pi += ANALOGTV.H;
				}
			}
//			System.out.println();
		}
	}
	private static void analogtv_draw(analogtv_ it, final BufferedImage image)
	{
		final DataBuffer imageBuf = image.getRaster().getDataBuffer();
		double[] raw_rgb = new double[it.subwidth * 3];
		analogtv_sync(it);
		double puheight = puramp(it,2.0,1.0,1.3) * it.height_control * (1.125 - 0.125 * puramp(it,2.0,2.0,1.1));
		analogtv_setup_levels(it,puheight * it.useheight / ANALOGTV.VISLINES);
		for (int lineno = ANALOGTV.TOP; lineno < ANALOGTV.BOT; lineno++)
		{
			final int slineno = lineno - ANALOGTV.TOP;
			int ytop = (int)((slineno * it.useheight / ANALOGTV.VISLINES - it.useheight / 2) * puheight) + it.useheight / 2;
			int ybot = (int)(((slineno + 1) * it.useheight / ANALOGTV.VISLINES - it.useheight / 2) * puheight) + it.useheight / 2;
			if (ytop == ybot)
				continue;
			if (ybot < 0 || ytop > it.useheight)
				continue;
			if (ytop < 0)
				ytop = 0;
			if (ybot > it.useheight)
				ybot = it.useheight;
			if (ybot > ytop + MAX_LINEHEIGHT)
				ybot = ytop + MAX_LINEHEIGHT;
			final double viswidth = ANALOGTV.PIC_LEN * 0.79;
			final double middle = ANALOGTV.PIC_LEN / 2;
			final int scanstart_i = (int)Math.rint(Math.floor((middle - viswidth * 0.5) * 65536.0));
//			System.out.println("scanstart_i: "+Integer.toHexString(scanstart_i)+": "+(middle - viswidth * 0.5));
			final int scanend_i = (ANALOGTV.PIC_LEN - 1) * 65536;
			final int isignal = ((lineno + it.cur_vsync + ANALOGTV.V) % ANALOGTV.V) * ANALOGTV.H + it.line_hsync[lineno];
			// System.out.println("scan:
			// "+(scanstart_i>>16)+"-"+(scanend_i>>16));
			analogtv_ntsc_to_yiq(it,lineno,isignal,(scanstart_i >> 16) - 10,(scanend_i >> 16) + 10);
			final double scanwidth = it.width_control * puramp(it,0.5,0.3,1.0);
//			System.out.println(scanwidth);
			final int pixrate = (int)Math.rint(Math.floor(viswidth * 65536.0 * 1.0 / it.subwidth / scanwidth));
//			System.out.println(pixrate);
			int scw = (int)Math.rint(Math.floor(it.subwidth * scanwidth));
			if (scw > it.subwidth)
				scw = it.usewidth;
			final int scl = it.subwidth / 2 - scw / 2;
			final int scr = it.subwidth / 2 + scw / 2;
			final int rgb_start = scl * 3;
			final int rgb_end = scr * 3;
			int rrp = rgb_start;
			int i = scanstart_i;
			while (i < 0 && rrp != rgb_end)
			{
				raw_rgb[rrp + 0] = raw_rgb[rrp + 1] = raw_rgb[rrp + 2] = 0.0;
				i += pixrate;
				rrp += 3;
			}
			final double pixbright = it.contrast_control * puramp(it,1.0,0.0,1.0) / (0.5 + 0.5 * puheight) * 1024.0 / 100.0;
			while (i < scanend_i && rrp != rgb_end)
			{
				final double pixfrac = (i & 0xffff) / 65536.0;
				final double invpixfrac = 1.0 - pixfrac;
				final int pati = i >> 16;
				final double interpy = (it.yiq[pati].y * invpixfrac + it.yiq[pati + 1].y * pixfrac);
				final double interpi = (it.yiq[pati].i * invpixfrac + it.yiq[pati + 1].i * pixfrac);
				final double interpq = (it.yiq[pati].q * invpixfrac + it.yiq[pati + 1].q * pixfrac);
				double r = (interpy + 0.948 * interpi + 0.624 * interpq) * pixbright;
				double g = (interpy - 0.276 * interpi - 0.639 * interpq) * pixbright;
				double b = (interpy - 1.105 * interpi + 1.729 * interpq) * pixbright;
				if (r < 0.0)
					r = 0.0;
				if (g < 0.0)
					g = 0.0;
				if (b < 0.0)
					b = 0.0;
				raw_rgb[rrp + 0] = r;
				raw_rgb[rrp + 1] = g;
				raw_rgb[rrp + 2] = b;
				// System.out.print("("+r+","+g+","+b+")");
				i += pixrate;
				rrp += 3;
			}
			while (rrp != rgb_end)
			{
				raw_rgb[rrp + 0] = raw_rgb[rrp + 1] = raw_rgb[rrp + 2] = 0.0;
				rrp += 3;
			}
			analogtv_blast_imagerow(it,raw_rgb,ytop,ybot,imageBuf);
		}
	}

	private static void analogtv_add_signal(analogtv_ it, analogtv_reception rec)
	{
		// int ec = it.channel_change_cycles;
		// System.arraycopy(rec.input.signal,0,rec.input.signal,ANALOGTV.V,ANALOGTV.H);
		// // ???
		// if (ec != 0)
		// {
		// int irxs = 0;
		// int isv = 0;
		// int ish = 0;
		// while (irxs < ANALOGTV.SIGNAL_LEN && ec > 0)
		// {
		// it.rx_signal[irxs] += rec.input.signal[isv][ish] * rec.level;
		// ++irxs;
		// ++ish;
		// if (ish >= ANALOGTV.H)
		// {
		// ish = 0;
		// ++isv;
		// }
		// if (isv >= ANALOGTV.V)
		// {
		// isv = 0;
		// }
		// --ec;
		// }
		// }
		// int isv = 0;
		// int ish = 0;
		// for (int ps = 0; ps < it.rx_signal.length; ps += 4)
		// {
		// double sig0 = rec.input.signal[isv][ish + 0];
		// double sig1 = rec.input.signal[isv][ish + 1];
		// double sig2 = rec.input.signal[isv][ish + 2];
		// double sig3 = rec.input.signal[isv][ish + 3];
		// it.rx_signal[ps + 0] += (sig0 + sig2 * rec.hfloss) * rec.level;
		// it.rx_signal[ps + 1] += (sig1 + sig3 * rec.hfloss) * rec.level;
		// it.rx_signal[ps + 2] += (sig2 + sig0 * rec.hfloss) * rec.level;
		// it.rx_signal[ps + 3] += (sig3 + sig1 * rec.hfloss) * rec.level;
		// ish += 4;
		// if (ish >= ANALOGTV.H)
		// {
		// ish = 0;
		// ++isv;
		// }
		// if (isv >= ANALOGTV.V)
		// {
		// isv = 0;
		// }
		// }
		// for (final double sig : it.rx_signal)
		// {
		// if (sig < -0.000001 || 0.000001 < sig)
		// System.out.println(""+sig);
		// }
		// it.rx_signal_level = Math.sqrt(it.rx_signal_level *
		// it.rx_signal_level + rec.level * rec.level);
		// it.channel_change_cycles = 0;
		int isv = 0;
		int ish = 0;
		for (int ps = 0; ps < it.rx_signal.length; ++ps)
		{
			it.rx_signal[ps] = rec.input.signal[ps];
//			++ish;
//			if (ish >= ANALOGTV.H)
//			{
//				ish = 0;
//				++isv;
//			}
//			if (isv >= ANALOGTV.V)
//			{
//				break;
//				// System.err.println("isv >= ANALOGTV.V isv: "+isv);
//				//				isv = 0;
//			}
		}
	}

	public AtomicBoolean shutdown = new AtomicBoolean();
	public void close() throws IOException
	{
		synchronized (this.shutdown)
		{
			this.shutdown.set(true);
			this.shutdown.notifyAll();
		}
	}
}
