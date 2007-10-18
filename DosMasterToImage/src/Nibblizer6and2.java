import java.util.Arrays;

/*
 * Created on Oct 18, 2007
 */
public class Nibblizer6and2
{
	/* static */ unsigned char DiskImg::kDiskBytes62[64] = {
	    0x96, 0x97, 0x9a, 0x9b, 0x9d, 0x9e, 0x9f, 0xa6,
	    0xa7, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb2, 0xb3,
	    0xb4, 0xb5, 0xb6, 0xb7, 0xb9, 0xba, 0xbb, 0xbc,
	    0xbd, 0xbe, 0xbf, 0xcb, 0xcd, 0xce, 0xcf, 0xd3,
	    0xd6, 0xd7, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde,
	    0xdf, 0xe5, 0xe6, 0xe7, 0xe9, 0xea, 0xeb, 0xec,
	    0xed, 0xee, 0xef, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6,
	    0xf7, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff
	};
	/*
	 * void DiskImg::EncodeNibble62(const CircularBufferAccess& buffer, int idx,
	 * const unsigned char* sctBuf, const NibbleDescr* pNibbleDescr) const {
	 * unsigned char top[256]; unsigned char twos[kChunkSize62]; int twoPosn,
	 * twoShift; int i;
	 * 
	 * memset(twos, 0, sizeof(twos));
	 * 
	 * twoShift = 0; for (i = 0, twoPosn = kChunkSize62-1; i < 256; i++) {
	 * unsigned int val = sctBuf[i]; top[i] = val >> 2; twos[twoPosn] |= ((val &
	 * 0x01) << 1 | (val & 0x02) >> 1) << twoShift;
	 * 
	 * if (twoPosn == 0) { twoPosn = kChunkSize62; twoShift += 2; } twoPosn--; }
	 * 
	 * int chksum = pNibbleDescr->dataChecksumSeed; for (i = kChunkSize62-1; i >=
	 * 0; i--) { assert(twos[i] < sizeof(kDiskBytes62)); buffer[idx++] =
	 * kDiskBytes62[twos[i] ^ chksum]; chksum = twos[i]; }
	 * 
	 * for (i = 0; i < 256; i++) { assert(top[i] < sizeof(kDiskBytes62));
	 * buffer[idx++] = kDiskBytes62[top[i] ^ chksum]; chksum = top[i]; }
	 * 
	 * //printf("Enc checksum value is 0x%02x\n", chksum); buffer[idx++] =
	 * kDiskBytes62[chksum]; }
	 */
		// Based on code by Andy McFadden, from CiderPress
		public static int[] encode_6and2(int[] data)
		{
			final int[] buffer = new int[0x100+BUF2_SIZ+1]; // TODO size???
			
		    final int[] top = new int[256];
		    final int[] twos = new int[GRP62];
	
		    Arrays.fill(twos,0);
	
		    int twoShift = 0;
		    for (int i = 0, twoPosn = GRP62-1; i < 256; i++) {
		        int val = sctBuf[i];
		        top[i] = val >> 2;
		        twos[twoPosn] |= ((val & 0x01) << 1 | (val & 0x02) >> 1) << twoShift;
	
		        if (twoPosn == 0) {
		            twoPosn = GRP62;
		            twoShift += 2;
		        }
		        twoPosn--;
		    }
	
		    int chksum = 0;
		    int idx = 0;
		    for (int i = GRP62-1; i >= 0; i--) {
		        assert(twos[i] < sizeof(kDiskBytes62));
		        buffer[idx++] = kDiskBytes62[twos[i] ^ chksum];
		        chksum = twos[i];
		    }
	
		    for (int i = 0; i < 256; i++) {
		        assert(top[i] < sizeof(kDiskBytes62));
		        buffer[idx++] = kDiskBytes62[top[i] ^ chksum];
		        chksum = top[i];
		    }
	
		    // printf("Enc checksum value is 0x%02x\n", chksum);
		    buffer[idx++] = kDiskBytes62[chksum];
		}
}
