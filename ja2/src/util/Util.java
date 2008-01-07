/*
 * Created on Nov 19, 2007
 */
package util;

import java.util.StringTokenizer;

public final class Util
{
	public static final int EOF = -1;

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
}
