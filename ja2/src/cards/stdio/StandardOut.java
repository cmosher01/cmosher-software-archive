/*
 * Created on Nov 28, 2007
 */
package cards.stdio;

import cards.Card;

public class StandardOut extends Card
{
	@Override
	public byte io(@SuppressWarnings("unused") final int addr, final byte data, @SuppressWarnings("unused") final boolean writing)
	{
		final char c = (char)(data&0x7F);
		if (c == '\r')
		{
			System.out.println();
		}
		else
		{
			System.out.print(c);
		}
		System.out.flush();

		return data;
	}

	/**
	 * @return
	 */
	@Override
	public String getTypeName()
	{
		return "writes to standard output";
	}
}
