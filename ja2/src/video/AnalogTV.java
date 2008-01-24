package video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import com.surveysampling.hash.SimpleHashAssocArray;
import com.surveysampling.hash.SimpleHashAssocArray.KeyNotFoundException;
import com.surveysampling.hash.SimpleHashAssocArray.NullKeyException;
import com.surveysampling.hash.SimpleHashAssocArray.NullValueException;

/*
 * Created on Jan 18, 2008
 */
public class AnalogTV
{
	private int[] signal = new int[AppleNTSC.SIGNAL_LEN];

//	private double agclevel = 1;
//	private double color_control = 1;

	private int isig;



	private static class analogtv_yiq
	{
		private final int y;
		private final int i;
		private final int q;
		private final int hash;

		public analogtv_yiq(final int y, final int i, final int q)
		{
			this.y = y;
			this.i = i;
			this.q = q;
			this.hash = getHash();
		}

		private int getHash()
		{
			return ((this.q+140)<<16) | ((this.i+140)<<8) | (this.y+140);
		}

		public int getI()
		{
			return this.i;
		}

		public int getQ()
		{
			return this.q;
		}

		public int getY()
		{
			return this.y;
		}

		@Override
		public int hashCode()
		{
			return this.hash;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof analogtv_yiq))
			{
				return false;
			}
			final analogtv_yiq that = (analogtv_yiq)obj;
			return this.y==that.y && this.i==that.i && this.q==that.q;
		}
	}



	public void dump_signal()
	{
//		int pi = 0;
//		for (int i = 0; i < this.signal.length; ++i)
//		{
//			System.out.print(pi==AppleNTSC.PIC_START ? "|" : " ");
//			System.out.printf("%+4d",this.signal[i]);
//			++pi;
//			if (pi >= AppleNTSC.H)
//			{
//				System.out.println();
//				pi = 0;
//			}
//		}
		final ArrayList<Integer> rSize = new ArrayList<Integer>(mapYIQtoRGB.getBucketCount());
		mapYIQtoRGB.getBucketSizes(rSize);
		for (Integer s: rSize)
		{
			System.out.println("bucket size: "+s);
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
		Arrays.fill(this.signal,0);

		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			int i = AppleNTSC.FP_START;
			if (lineno < VideoAddressing.VISIBLE_ROWS_PER_FIELD)
			{
				while (i < AppleNTSC.SYNC_START)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.BLANK_LEVEL;
				}
				while (i < AppleNTSC.BP_START)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.SYNC_LEVEL;
				}
				while (i < AppleNTSC.PIC_START)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.BLANK_LEVEL;
				}
				while (i < AppleNTSC.H)
				{
					this.signal[lineno * AppleNTSC.H + i++] = AppleNTSC.BLANK_LEVEL;//AppleNTSC.BLACK_LEVEL;
				}

				// add in the color burst
//				for (int icb = AppleNTSC.CB_START; icb < AppleNTSC.CB_END; icb += 4)
//				{
//					this.signal[lineno * AppleNTSC.H + icb + 1] = -AppleNTSC.CB_LEVEL;
//					this.signal[lineno * AppleNTSC.H + icb + 3] = AppleNTSC.CB_LEVEL;
//				}
				for (int icb = AppleNTSC.CB_START; icb < AppleNTSC.CB_END; icb += 4)
				{
					this.signal[lineno * AppleNTSC.H + icb + 0] = -AppleNTSC.CB_LEVEL/2;
					this.signal[lineno * AppleNTSC.H + icb + 1] = -AppleNTSC.CB_LEVEL/2;
					this.signal[lineno * AppleNTSC.H + icb + 2] = AppleNTSC.CB_LEVEL/2;
					this.signal[lineno * AppleNTSC.H + icb + 3] = AppleNTSC.CB_LEVEL/2;
				}

				// add unwanted spike on back porch
				// (only for Rev. 0 boards)
				this.signal[lineno * AppleNTSC.H + AppleNTSC.SPIKE] = -AppleNTSC.CB_LEVEL;
			}
			else
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
		}

		this.isig = 0;
	}

	public void write_signal(final int level)
	{
		if (this.isig >= AppleNTSC.SIGNAL_LEN)
		{
			throw new IllegalStateException("At end of screen; must re-synch before writing any more signal");
		}
		this.signal[this.isig++] = level;
	}

	public void skip()
	{
		if (this.isig >= AppleNTSC.SIGNAL_LEN)
		{
			throw new IllegalStateException("At end of screen; must re-synch before writing any more signal");
		}
		++this.isig;
	}

	public void skip(int s)
	{
		this.isig += s;
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

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 500] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 501] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 502] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 503] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 504] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 505] = 100;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 506] = 100;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 550] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 551] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 552] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 553] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 554] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 555] = 50;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 556] = 50;
		}
	}

	public void write_play_signal5()
	{
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 100] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 201] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 302] = 50;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 403] = 50;



			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 152] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 153] = 25;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 253] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 254] = 25;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 354] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 355] = 25;

			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 455] = 25;
			this.signal[lineno * AppleNTSC.H + AppleNTSC.PIC_START + 456] = 25;
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
	
	public void test_draw(final BufferedImage image)
	{
		final DataBuffer imageBuf = image.getRaster().getDataBuffer();

		int pi = 0;
//		final analogtv_yiq[] yiq = new analogtv_yiq[AppleNTSC.H];
		final int[] yiq = new int[AppleNTSC.H];
		for (int lineno = 0; lineno < AppleNTSC.V; ++lineno)
		{
			final CB cb = get_cb(lineno);
			final IQ iq_factor = get_iq_factor(cb);
			ntsc_to_yiq(lineno * AppleNTSC.H,iq_factor,yiq);
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
	private static final Map<CB,IQ> cacheCB = new HashMap<CB,IQ>(2,1);

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
	private double[] get_cb_phase(CB cb)
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

	private static final double COLOR_THRESH = 2.8;
	private static final double COLOR_BASE = 104;

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
			return new IQ();
		}
//		System.out.printf("%+8.2f,%+8.2f,%+8.2f,%+8.2f\n",cb_phase[0],cb_phase[1],cb_phase[2],cb_phase[3]);

		final double[] iq_factor = new double[4];

		final double tint_i = -Math.cos((COLOR_BASE) * Math.PI / 180);
		final double tint_q = +Math.sin((COLOR_BASE) * Math.PI / 180);
		iq_factor[0] = (cb_i * tint_i - cb_q * tint_q);
		iq_factor[2] = -iq_factor[0];
		iq_factor[1] = (cb_q * tint_i + cb_i * tint_q);
		iq_factor[3] = -iq_factor[1];

//		System.out.printf("%+8.2f,%+8.2f,%+8.2f,%+8.2f\n",iq_factor[0],iq_factor[1],iq_factor[2],iq_factor[3]);
		final IQ iq = new IQ(iq_factor);
		cacheCB.put(cb,iq);
		return iq;
	}

	private final SortedSet<Integer> catalog = new TreeSet<Integer>();

//	private void ntsc_to_yiq(final int isignal, final IQ iq_factor, final analogtv_yiq[] yiq)
	private void ntsc_to_yiq(final int isignal, final IQ iq_factor, final int[] yiq)
	{
		final Lowpass_3_58_MHz filterY = new Lowpass_3_58_MHz();
		final Lowpass_1_5_MHz filterI = new Lowpass_1_5_MHz();
		final Lowpass_1_5_MHz filterQ = new Lowpass_1_5_MHz();
		for (int off = 0; off < AppleNTSC.H; ++off)
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
				i = (int)(filterI.transition(sig * iq_factor.get(off & 3)));
				q = (int)(filterQ.transition(sig * iq_factor.get((off + 3) & 3)));
			}

//			yiq[off] = new analogtv_yiq(y,i,q);
			yiq[off] = (((q+140)&0xff) << 16) | (((i+140)&0xff) << 8) | ((y+140)&0xff);
		}
	}

	public void dumpYs()
	{
		for (final Integer y : this.catalog)
		{
			System.out.println(y);
		}
	}

//	private static SimpleHashAssocArray<analogtv_yiq,Integer> mapYIQtoRGB = new SimpleHashAssocArray<analogtv_yiq,Integer>(1013);
	private static SimpleHashAssocArray<Integer,Integer> mapYIQtoRGB = new SimpleHashAssocArray<Integer,Integer>(1013);
//	private static int yiq2rgb(final analogtv_yiq yiq)
	private static int yiq2rgb(final int yiq)
	{
		if (mapYIQtoRGB.containsKey(yiq))
		{
			try
			{
				return mapYIQtoRGB.get(yiq);
			}
			catch (KeyNotFoundException e)
			{
				throw new IllegalStateException(e);
			}
		}
//		double r = yiq.getY() + 0.956 * yiq.getI() + 0.621 * yiq.getQ();
//		double g = yiq.getY() - 0.272 * yiq.getI() - 0.647 * yiq.getQ();
//		double b = yiq.getY() - 1.105 * yiq.getI() + 1.702 * yiq.getQ();
		double r = ((yiq&0xFF)-140) + 0.956 * (((yiq>>8)&0xFF)-140) + 0.621 * (((yiq>>16)&0xFF)-140);
		double g = ((yiq&0xFF)-140) - 0.272 * (((yiq>>8)&0xFF)-140) - 0.647 * (((yiq>>16)&0xFF)-140);
		double b = ((yiq&0xFF)-140) - 1.105 * (((yiq>>8)&0xFF)-140) + 1.702 * (((yiq>>16)&0xFF)-140);

		r *= 1.3;
		g *= 1.3;
		b *= 1.3;

		final int rgb =
			(calc_color(r) << 16)| 
			(calc_color(g) <<  8)| 
			(calc_color(b) <<  0);

		try
		{
//			System.out.printf("y,i,q,h,b: %+5d,%+5d,%+5d,%8x, %4d\n",yiq.y,yiq.i,yiq.q,yiq.hash,yiq.hash%1013);
			mapYIQtoRGB.put(yiq,rgb);
		}
		catch (Throwable e)
		{
			throw new IllegalStateException(e);
		}

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
}
