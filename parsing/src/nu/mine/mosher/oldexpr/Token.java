/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.oldexpr;

public abstract class Token
{
	private final String value;

	public Token(final String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return this.value;
	}
}
