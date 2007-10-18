import java.util.Arrays;

/*
 * Created on Oct 18, 2007
 */
public class Nibblizer6and2
{
	private static final int GRP = 0x56;

    private static final int BUF2_SIZ = 0x9A;

    private static final int[] xlate = new int[]
	{
								0x96, 0x97,       0x9A, 0x9B,       0x9D, 0x9E, 0x9F,
								0xA6, 0xA7,     /*0xAA*/0xAB, 0xAC, 0xAD, 0xAE, 0xAF,
		0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 0xB7, 0xB9, 0xBA, 0xBB, 0xBC, 0xBD, 0xBE, 0xBF,
														0xCB,       0xCD, 0xCE, 0xCF,
		      0xD3,     /*0xD5*/0xD6, 0xD7, 0xD9, 0xDA, 0xDB, 0xDC, 0xDD, 0xDE, 0xDF,
		                  0xE5, 0xE6, 0xE7, 0xE9, 0xEA, 0xEB, 0xEC, 0xED, 0xEE, 0xEF,
		0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF9, 0xFA, 0xFB, 0xFC, 0xFD, 0xFE, 0xFF
	};

	// Based on code by Andy McFadden, from CiderPress
	public static int[] encode_6and2(int[] data)
	{
		final int[] buffer = new int[0x100 + BUF2_SIZ + 1];

		final int[] top = new int[0x100];
		final int[] two = new int[GRP];

		Arrays.fill(two,0);

		for (int i = 0, twoPosn = GRP-1, twoShift = 0; i < 256; i++)
		{
			int val = data[i];
			top[i] = val >> 2;
			two[twoPosn] |= ((val & 0x01) << 1 | (val & 0x02) >> 1) << twoShift;
			if (twoPosn == 0)
			{
				twoPosn = GRP;
				twoShift += 2;
			}
			twoPosn--;
		}

		int chksum = 0;
		int idx = 0;
		for (int i = GRP-1; i >= 0; i--)
		{
			buffer[idx++] = xlate[two[i] ^ chksum];
			chksum = two[i];
		}
		for (int i = 0; i < 256; i++)
		{
			buffer[idx++] = xlate[top[i] ^ chksum];
			chksum = top[i];
		}

		buffer[idx++] = xlate[chksum];

		return buffer;
	}
}
