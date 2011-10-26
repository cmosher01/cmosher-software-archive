package nu.mine.mosher.unicode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Converts a UTF-32 string (given as an array of ints) into a UTF-16 string.
 * @author Chris Mosher
 */
public class ConvertUTF
{
	/*
	 * 2.1 Encoding UTF-16
	 * 
	 * 	   Encoding of a single character from an ISO 10646 character value to
	 * 	   UTF-16 proceeds as follows. Let U be the character number, no greater
	 * 	   than 0x10FFFF.
	 * 
	 * 	   1) If U < 0x10000, encode U as a 16-bit unsigned integer and
	 * 		  terminate.
	 * 
	 * 	   2) Let U' = U - 0x10000. Because U is less than or equal to 0x10FFFF,
	 * 		  U' must be less than or equal to 0xFFFFF. That is, U' can be
	 * 		  represented in 20 bits.
	 * 
	 * 	   3) Initialize two 16-bit unsigned integers, W1 and W2, to 0xD800 and
	 * 		  0xDC00, respectively. These integers each have 10 bits free to
	 * 		  encode the character value, for a total of 20 bits.
	 * 
	 * 	   4) Assign the 10 high-order bits of the 20-bit U' to the 10 low-order
	 * 		  bits of W1 and the 10 low-order bits of U' to the 10 low-order
	 * 		  bits of W2. Terminate.
	 * 
	 * 	   Graphically, steps 2 through 4 look like:
	 * 	   U' = yyyyyyyyyyxxxxxxxxxx
	 * 	   W1 = 110110yyyyyyyyyy
	 * 	   W2 = 110111xxxxxxxxxx
	 */
	public static String convert32to16(String32 s32)
	{
		int[] utf32 = s32.get();
		StringBuffer sb = new StringBuffer(2*utf32.length);
		for (int i = 0; i < utf32.length; ++i)
        {
            int c = utf32[i];

			if (c > 0xffff)
			{
				if (c > 0x10FFFF)
					throw new IllegalArgumentException("Cannot encode character 0x"+Integer.toHexString(c)+" in UTF-16.");

				// encode UTF-32 character as two UTF-16 words
				int w1 = 0xD800;
				int w2 = 0xDC00;

				int uprime = c-0x10000;
				w2 |= uprime & 0x3ff;
				uprime >>= 10;
				w1 |= uprime & 0x3ff;

				sb.append((char)w1);
				sb.append((char)w2);
			}
			else
			{
				sb.append((char)c);
			}
        }
        return sb.toString();
	}

	/*
	 * 2.2 Decoding UTF-16
	 * 
	 *     Decoding of a single character from UTF-16 to an ISO 10646 character
	 *     value proceeds as follows. Let W1 be the next 16-bit integer in the
	 *     sequence of integers representing the text. Let W2 be the (eventual)
	 *     next integer following W1.
	 *  
	 *     1) If W1 < 0xD800 or W1 > 0xDFFF, the character value U is the value
	 *  	  of W1. Terminate.
	 *  
	 *     2) Determine if W1 is between 0xD800 and 0xDBFF. If not, the sequence
	 *  	  is in error and no valid character can be obtained using W1.
	 *  	  Terminate.
	 *  
	 *     3) If there is no W2 (that is, the sequence ends with W1), or if W2
	 *  	  is not between 0xDC00 and 0xDFFF, the sequence is in error.
	 *  	  Terminate.
	 *  
	 *     4) Construct a 20-bit unsigned integer U', taking the 10 low-order
	 *  	  bits of W1 as its 10 high-order bits and the 10 low-order bits of
	 *  	  W2 as its 10 low-order bits.
	 *  
	 *     5) Add 0x10000 to U' to obtain the character value U. Terminate.
	 *  
	 *     Note that steps 2 and 3 indicate errors. Error recovery is not
	 *     specified by this document. When terminating with an error in steps 2
	 *     and 3, it may be wise to set U to the value of W1 to help the caller
	 *     diagnose the error and not lose information. Also note that a string
	 *     decoding algorithm, as opposed to the single-character decoding
	 *     described above, need not terminate upon detection of an error, if
	 *     proper error reporting and/or recovery is provided.
	 */
	public static String32 convert16to32(String s)
	{
		List rutf32 = new ArrayList(s.length()*2);
		for (int i = 0; i < s.length(); ++i)
		{
			int utf32;
			char w1 = s.charAt(i);
			if (0xd800 <= w1 && w1 < 0xe000)
			{
				if (0xdc00 <= w1)
				{
					throw new IllegalArgumentException("Invalid first word of UTF-16 encoding: 0x"+Integer.toHexString(w1));
				}

				++i;

				if (i >= s.length())
				{
					throw new IllegalArgumentException("End of string after first word of UTF-16 encoding: 0x"+Integer.toHexString(w1));
				}

				char w2 = s.charAt(i);

				if (w2 < 0xdc00 || 0xe000 <= w2)
				{
					throw new IllegalArgumentException("Invalid second word of UTF-16 encoding: 0x"+Integer.toHexString(w2));
				}

				w1 &= 0x3ff;
				w2 &= 0x3ff;

				utf32 = w1;
				utf32 <<= 10;
				utf32 |= w2;
				utf32 += 0x10000;

			}
			else
			{
				utf32 = w1;
				utf32 &= 0x0000ffff;
			}

			rutf32.add(new Integer(utf32));
		}
		return new String32(intListToArray(rutf32));
	}

	private static int[] intListToArray(List list)
	{
		int[] r = new int[list.size()];
		int c = 0;
		for (Iterator i = list.iterator(); i.hasNext();)
		{
			Integer x = (Integer)i.next();
			r[c++] = x.intValue();
		}
		return r;
	}
}
