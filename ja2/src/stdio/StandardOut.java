/*
 * Created on Nov 28, 2007
 */
package stdio;

import chipset.Card;

public class StandardOut extends Card
{
	public byte io(@SuppressWarnings("unused") final int addr, final byte data)
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
}
