/*
 * Created on Nov 28, 2007
 */
package stdio;

import chipset.Card;

public class StandardOut extends Card
{
	public StandardOut()
	{
		this.seventhRom.write(0,(byte)1);
	}
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

		return (byte)c;
	}
}
