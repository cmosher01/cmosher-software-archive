package video;

import chipset.Clock;
import util.HexUtil;


public class VideoAddressing
{
	private static final int NTSC_LINES_PER_FRAME = 3*5*5*7;
	public static final int NTSC_LINES_PER_FIELD = NTSC_LINES_PER_FRAME/2;
	private static final int NTSC_FIELDS_PER_SECOND = 60;
	private static final int NTSC_COLOR_FIELD_EVERY = 1000;

	private static final int APPLE_BYTES = (NTSC_COLOR_FIELD_EVERY+1)*Clock.CPU_HZ;
	private static final int LINES = NTSC_FIELDS_PER_SECOND*NTSC_COLOR_FIELD_EVERY*NTSC_LINES_PER_FIELD;
	public static final int BYTES_PER_ROW = APPLE_BYTES/LINES;

	public static final int BYTES_PER_FIELD = BYTES_PER_ROW*NTSC_LINES_PER_FIELD;

	public static final int VISIBLE_BYTES_PER_ROW = 40;
	private static final int BLANKED_BYTES_PER_ROW = BYTES_PER_ROW-VISIBLE_BYTES_PER_ROW;
	public static final int VISIBLE_ROWS_PER_FIELD = 192;
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

	private static int calc(final int t)
	{
		int c = t % VISIBLE_BYTES_PER_FIELD;
		if (t >= SCANNABLE_BYTES)
		{
			c -= RESET_BYTES;
		}

		int n = c / BYTES_PER_ROW;
		final int s = (n/0x40);
		n -= s*0x40;
		final int q = (n/8);
		n -= q*8;
		final int base = 0x400*n+0x80*q+VISIBLE_BYTES_PER_ROW*s;

		final int half_page = base & 0xFF80;

		int a = base+(c%BYTES_PER_ROW)-BLANKED_BYTES_PER_ROW;
		if (a < half_page)
		{
			a += 0x80;
		}
		return a;
	}

	public static void main(final String... args)
	{
		final int[] lut = buildLUT(0x2000,0x2000);
		int t = 0;
		for (int y = 0; y < NTSC_LINES_PER_FIELD; ++y)
		{
			for (int x = 0; x < BYTES_PER_ROW; ++x)
			{
				System.out.print(HexUtil.word(lut[t++]));
				System.out.print(",");
			}
			System.out.println();
		}
	}
}
