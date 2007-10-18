/*
 * Created on 2007-10-18
 */
public class Nibblizer4and4
{
	private Nibblizer4and4() { throw new IllegalStateException(); }

	public static int encode(final int n)
    {
    	// input byte:  hgfedcba
    	// output word: 1g1e1c1a1h1f1d1b

		if (!(0 <= n && n < 0x100))
    	{
    		throw new IllegalArgumentException("invalid byte: "+n);
    	}

		//   hgfedcba00000000
		//   000000000hgfedcb
		// | 1010101010101010
		// ------------------
    	//   1g1e1c1a1h1f1d1b
		return (n << 8) | (n >> 1) | 0xAAAA;
    }
}
