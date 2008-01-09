/*
 * Created on Nov 19, 2007
 */
package util;

import java.util.StringTokenizer;

public final class Util
{
	public static final int EOF = -1;
	public static final byte bEOF = -1;

	private Util() { throw new IllegalStateException(); }

	public static int divideRound(final int dividend, final int divisor)
	{
		return (dividend+divisor/2)/divisor;
	}

	public static int divideRoundUp(final int dividend, final int divisor)
	{
		return (dividend+divisor-1)/divisor;
	}

	public static String nextTok(final StringTokenizer tok)
	{
		if (!tok.hasMoreTokens())
		{
			return "";
		}
		return tok.nextToken();
	}

	public static int mod(int x, final int m)
	{
		x %= m;
		if (x < 0)
		{
			x += m;
		}
		return x;
	}

	public static int constrain(final int min, int x, final int lim)
	{
		if (x < min)
		{
			x = min;
		}
		else if (lim <= x)
		{
			x = lim-1;
		}

		return x;
	}
}
