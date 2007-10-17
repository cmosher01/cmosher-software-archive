/*
 * Created on Oct 15, 2007
 */
public class Nibblizer
{
    private static final int GRP = 0x33;

    private static final int BUF2_SIZ = 3*GRP+1;

    private static final int[] xlate = new int[]
    {
                        /*0xAA*/0xAB, 0xAD, 0xAE, 0xAF,
        0xB5, 0xB6, 0xB7, 0xBA, 0xBB, 0xBD, 0xBE, 0xBF,
      /*0xD5*/0xD6, 0xD7, 0xDA, 0xDB, 0xDD, 0xDE, 0xDF,
                          0xEA, 0xEB, 0xED, 0xEE, 0xEF,
        0xF5, 0xF6, 0xF7, 0xFA, 0xFB, 0xFD, 0xFE, 0xFF,
    };

    public static int[] encode_5and3_alternate(final int[] data)
	{
		final int[] enc = new int[0x100+BUF2_SIZ+1];

		buildBuf(data,enc);
		flipBuf2(enc);
		xorBuf(enc);
		xlateBuf(enc);

		return enc;
	}

	private static void buildBuf(final int[] data, final int[] enc)
	{
		int base = 0;

		for (int i = 0; i < GRP; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP+i] & 0x04) >> 1;
			enc[base+i] |= (data[4*GRP+i] & 0x04) >> 2;
		}
		base += GRP;

		for (int i = 0; i < GRP; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP+i] & 0x02);
			enc[base+i] |= (data[4*GRP+i] & 0x02) >> 1;
		}
		base += GRP;

		for (int i = 0; i < GRP; ++i)
		{
			enc[base+i]  = (data[base+i] & 0x07) << 2;
			enc[base+i] |= (data[3*GRP+i] & 0x01) << 1;
			enc[base+i] |= (data[4*GRP+i] & 0x01);
		}
		base += GRP;

		enc[base] = 0;
		++base;

		for (int i = 0; i < 5*GRP; ++i)
		{
			enc[base+i] = data[i] >> 3;
		}
		base += 5*GRP;

		enc[base] = data[5*GRP] & 0x1F; // throw out high 3 bits
	}

	private static void flipBuf2(final int[] enc)
	{
		int sw = BUF2_SIZ;
		for (int i = 0; i < BUF2_SIZ/2; ++i)
		{
			--sw;
			int tmp = enc[i];
			enc[i] = enc[sw];
			enc[sw] = tmp;
		}
	}

	private static void xorBuf(final int[] enc)
	{
		enc[enc.length-1] = 0;
		for (int i = enc.length-1; i > 0; --i)
		{
			enc[i] = enc[i-1] ^ enc[i];
		}
	}

	private static void xlateBuf(final int[] enc)
	{
		for (int i = 0; i < enc.length; ++i)
		{
			enc[i] = xlate[enc[i]];
		}
	}

	public static int[] encode_5and3(int[] data)
	{
		final int[] buffer = new int[0x100+BUF2_SIZ+1];

	    final int[] top = new int[5*GRP+1];
	    final int[] thr = new int[3*GRP+1];

	    /*
		 * Split the bytes into sections.
		 */
	    int chunk = GRP-1;
	    int sctBuf = 0;
	    for (int i = 0; i < top.length-1; i += 5)
	    {
	        int three1 = data[sctBuf++];
	        int three2 = data[sctBuf++];
	        int three3 = data[sctBuf++];
	        int three4 = data[sctBuf++];
	        int three5 = data[sctBuf++];

	        top[chunk+0*GRP] = three1 >> 3;
	        top[chunk+1*GRP] = three2 >> 3;
	        top[chunk+2*GRP] = three3 >> 3;
	        top[chunk+3*GRP] = three4 >> 3;
	        top[chunk+4*GRP] = three5 >> 3;

	        thr[chunk+0*GRP] = (three1 & 0x07) << 2 | (three4 & 0x04) >> 1 | (three5 & 0x04) >> 2;
	        thr[chunk+1*GRP] = (three2 & 0x07) << 2 | (three4 & 0x02)      | (three5 & 0x02) >> 1;
	        thr[chunk+2*GRP] = (three3 & 0x07) << 2 | (three4 & 0x01) << 1 | (three5 & 0x01);

	        --chunk;
	    }

	    /*
		 * Handle the last byte.
		 */
	    int val = data[sctBuf++];
	    top[5*GRP] = val >> 3;
	    thr[3*GRP] = val & 0x07;

	    /*
		 * Write the bytes.
		 */
	    int chksum = 0;
	    int idx = 0;
	    for (int i = thr.length-1; i >= 0; --i)
	    {
	        assert(thr[i] < xlate.length);
	        buffer[idx++] = xlate[thr[i] ^ chksum];
	        chksum = thr[i];
	    }

	    for (int i = 0; i < top.length; ++i)
	    {
	        assert(top[i] < xlate.length);
	        buffer[idx++] = xlate[top[i] ^ chksum];
	        chksum = top[i];
	    }

	    buffer[idx++] = xlate[chksum];

	    return buffer;
	}
}
