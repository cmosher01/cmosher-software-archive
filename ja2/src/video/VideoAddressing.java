package video;

import chipset.Clock;



class VideoAddressing
{
	private static final int NTSC_LINES_PER_FRAME = 3*5*5*7;
	static final int NTSC_LINES_PER_FIELD = NTSC_LINES_PER_FRAME/2;
	private static final int NTSC_FIELDS_PER_SECOND = 60;
	private static final int NTSC_COLOR_FIELD_EVERY = 1000;

	private static final int APPLE_BYTES = (NTSC_COLOR_FIELD_EVERY+1)*Clock.CPU_HZ;
	private static final int LINES = NTSC_FIELDS_PER_SECOND*NTSC_COLOR_FIELD_EVERY*NTSC_LINES_PER_FIELD;
	public static final int BYTES_PER_ROW = APPLE_BYTES/LINES;

	public static final int BYTES_PER_FIELD = BYTES_PER_ROW*NTSC_LINES_PER_FIELD;

	public static final int VISIBLE_BITS_PER_BYTE = 7;
	private static final int VISIBLE_LINES_PER_CHARACTER = 8;

	public static final int VISIBLE_BYTES_PER_ROW = calculateVisibleCharactersPerRow();
	public static final int VISIBLE_ROWS_PER_FIELD = calculateVisibleRows();

	private static final int BLANKED_BYTES_PER_ROW = BYTES_PER_ROW-VISIBLE_BYTES_PER_ROW;
	private static final int VISIBLE_BYTES_PER_FIELD = BYTES_PER_ROW*VISIBLE_ROWS_PER_FIELD;
	private static final int SCANNABLE_ROWS = 0x100;
	private static final int SCANNABLE_BYTES = SCANNABLE_ROWS*BYTES_PER_ROW;
	private static final int RESET_ROWS = NTSC_LINES_PER_FIELD-SCANNABLE_ROWS;
	private static final int RESET_BYTES = RESET_ROWS*BYTES_PER_ROW;

	private static final int MEGA = 1000000;

	static int[] buildLUT(final int base, final int len)
	{
		final int[] lut = new int[BYTES_PER_FIELD];
		for (int t = 0; t < lut.length; ++t)
		{
			final int col = t % BYTES_PER_ROW;
			final int row = t / BYTES_PER_ROW;
			int off = 0;
			if (col < BLANKED_BYTES_PER_ROW)
			{
				// HBL
				if (base < 0x1000)
				{
					off += 0x1000;
				}
				if (col == 0)
				{
					off += 1;
				}
			}

			lut[t] = base + (calc(t) % len) + off;

			if (row >= VISIBLE_ROWS_PER_FIELD)
			{
				// VBL
				lut[t] -= 8; // TODO must do mod 128 subtraction here
			}
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
		 *                                                                                    10   81
		 * horizontal blanking period = (1.5+4.7+.6+2.5+1.6) = 10.9 microseconds per line = ( -- + -- ) microseconds per line
		 *                                                                                         90
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
		return ((int)Math.rint(((double)(NTSC_COLOR_FIELD_EVERY+1)/(NTSC_FIELDS_PER_SECOND*NTSC_COLOR_FIELD_EVERY)*2/(NTSC_LINES_PER_FRAME)*MEGA - (1.5+4.7+.6+2.5+1.6)) *
			.75 *
			(Clock.CRYSTAL_HZ/2))) / MEGA / VISIBLE_BITS_PER_BYTE;
	}
	private static int calculateVisibleRows()
	{
		/*
		 * NTSC total lines per frame (525) minus unusable lines (19 plus 20) = 486 usable lines
		 * To avoid the over-scan area, use the middle 80% of the vertical lines, giving 388 (rounded down) clearly visible lines
		 * Apple ][ uses only half the vertical resolution because it doesn't interlace, giving 194.
		 * Text characters are 8 pixels tall, so 194/8 rounded down gives 24 text lines.
		 * Multiply by 8 to give 192 lines total.
		 */
		return ((int)Math.rint((NTSC_LINES_PER_FRAME-(20+19))*.8))/2/VISIBLE_LINES_PER_CHARACTER*VISIBLE_LINES_PER_CHARACTER;
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
