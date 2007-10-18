/*
 * Created on 2007-10-18
 */
public class Nibblizer4and4
{
	private Nibblizer4and4() { throw new IllegalStateException(); }

	public static int encode(final int byte_value)
    {
    	// input byte:  hgfedcba
    	// output word: 1g1e1c1a1h1f1d1b

		if (!(0 <= byte_value && byte_value < 0x100))
    	{
    		throw new IllegalArgumentException("invalid byte: "+byte_value);
    	}

		return (byte_value << 8) | (byte_value >> 1) | 0xAAAA;
    }
}
