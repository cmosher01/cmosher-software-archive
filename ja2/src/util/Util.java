/*
 * Created on Nov 19, 2007
 */
package util;

public final class Util
{
	private Util() { throw new IllegalStateException(); }

	public static int divideRound(final int dividend, final int divisor)
	{
		return (dividend+divisor/2)/divisor;
	}

	public static int divideRoundUp(final int dividend, final int divisor)
	{
		return (dividend+divisor-1)/divisor;
	}
}
