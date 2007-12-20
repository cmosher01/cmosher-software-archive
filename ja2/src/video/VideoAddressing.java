package video;

import chipset.Clock;



class VideoAddressing
{
	private static final int NTSC_LINES_PER_FRAME = 3*5*5*7;
	public static final int NTSC_LINES_PER_FIELD = NTSC_LINES_PER_FRAME/2;
	private static final int NTSC_FIELDS_PER_SECOND = 60;
	private static final int NTSC_COLOR_FIELD_EVERY = 1000;

	private static final int APPLE_BYTES = (NTSC_COLOR_FIELD_EVERY+1)*Clock.CPU_HZ;
	private static final int LINES = NTSC_FIELDS_PER_SECOND*NTSC_COLOR_FIELD_EVERY*NTSC_LINES_PER_FIELD;
	public static final int BYTES_PER_ROW = APPLE_BYTES/LINES;

	public static final int BYTES_PER_FIELD = BYTES_PER_ROW*NTSC_LINES_PER_FIELD;

	public static final int VISIBLE_BYTES_PER_ROW = calculateVisibleCharactersPerRow();
	private static final int BLANKED_BYTES_PER_ROW = BYTES_PER_ROW-VISIBLE_BYTES_PER_ROW;
	public static final int VISIBLE_ROWS_PER_FIELD = calculateVisibleRows();
	private static final int VISIBLE_BYTES_PER_FIELD = BYTES_PER_ROW*VISIBLE_ROWS_PER_FIELD;
	private static final int SCANNABLE_ROWS = 0x100;
	private static final int SCANNABLE_BYTES = SCANNABLE_ROWS*BYTES_PER_ROW;
	private static final int RESET_ROWS = NTSC_LINES_PER_FIELD-SCANNABLE_ROWS;
	private static final int RESET_BYTES = RESET_ROWS*BYTES_PER_ROW;

	static int[] buildLUT(final int base, final int len)
	{
		final int[] lut = new int[BYTES_PER_FIELD];
		for (int t = 0; t < lut.length; ++t)
		{
			lut[t] = base + (calc(t) % len);
		}
		return lut;
	}

	private static int calculateVisibleCharactersPerRow()
	{
		/*
		 *                                1000+1 seconds   2 fields   1 frame         1000000 microseconds         63   50
		 * total horizontal line period = -------------- * -------- * ------------- * --------------------   =   ( -- + -- ) microseconds per line
		 *                                60*1000 fields   1 frame    3*5*5*7 lines   1 second                          90
		 *
		 *                                                             10   81
		 * horizontal blanking period = 10.9 microseconds per line = ( -- + -- ) microseconds per line
		 *                                                                  90
		 *
		 * visible line period = total horizontal line period minus horizontal blanking period =
		 * 
		 * 52   59
		 * -- + -- microseconds per line
		 *      90
		 *
		 *
		 * To avoid the over-scan area, the Apple ][ uses only the middle 75% of the visible line, or 4739/120 microseconds
		 * 
		 * Apple ][ uses half the clock rate, or 315/44 MHz, to oscillate the video signal.
		 * 
		 * The result is 315/44 MHz * 4739/120 microseconds/line, rounded down, = 282 full pixel spots across the screen.
		 * The Apple ][ displays 7 bits per byte hi-res or lo-res, (or 7 pixel-wide characters for text mode), so that
		 * gives 282/7, which rounds down to 40 bytes per line.
		 */
		return ((int)Math.rint(((double)(NTSC_COLOR_FIELD_EVERY+1)/(NTSC_FIELDS_PER_SECOND*NTSC_COLOR_FIELD_EVERY)*2/(3*5*5*7)*1000000 - (1.5+4.7+.6+2.5+1.6)) *
			.75 *
			(Clock.CRYSTAL_HZ/2))) / 1000000 / 7;
	}
	private static int calculateVisibleRows()
	{
		return ((int)Math.rint(486*.8))/2/8*8;
	}

	private static int calc(final int t)
	{
		int c = t % VISIBLE_BYTES_PER_FIELD;
		if (t >= SCANNABLE_BYTES)
		{
			c -= RESET_BYTES;
		}

		int n = c / BYTES_PER_ROW;
		final int s = (n >> 6);
		n -= s << 6;
		final int q = (n >> 3);
		n -= q << 3;
		final int base = (n<<10) + (q<<7) + VISIBLE_BYTES_PER_ROW*s;

		final int half_page = base & 0xFF80;

		int a = base+(c%BYTES_PER_ROW)-BLANKED_BYTES_PER_ROW;
		if (a < half_page)
		{
			a += 0x80;
		}
		return a;
	}
}
