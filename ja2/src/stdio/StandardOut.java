/*
 * Created on Nov 28, 2007
 */
package stdio;

import chipset.Card;

public class StandardOut implements Card
{
	public byte io(final int addr, final byte data)
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

		return (byte)c;
	}
}
