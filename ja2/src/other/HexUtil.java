package other;
/*
 * Created on Sep 16, 2007
 */
public class HexUtil
{
	public static String byt(final byte byt)
	{
		final int x = byt & 0xFF;
		final StringBuilder s = new StringBuilder(2);
		toHex(x,2,s);
		return s.toString();
	}

	public static String word(final int word)
	{
		final int x = word & 0xFFFF;
		final StringBuilder s = new StringBuilder(4);
		toHex(x,4,s);
		return s.toString();
	}

	public static void toHex(int i, int minLen, StringBuilder s)
    {
        String hex = Integer.toHexString(i);
        hex = hex.toUpperCase();
        rep('0',minLen-hex.length(),s);
        s.append(hex);
    }

    public static void rep(char c, int len, StringBuilder s)
    {
        for (int i = 0; i < len; ++i)
            s.append(c);
    }
}
