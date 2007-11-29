/*
 * Created on Nov 28, 2007
 */
package stdio;

import java.io.IOException;
import chipset.Card;

public class StandardIn implements Card
{
	private static final int EOF = -1;

	private int latch;
	private boolean gotCR;
	private boolean gotEOF;

	public byte io(final int addr, final byte data)
	{
		byte r = -1;
		final int sw = addr & 0x0F;
		if (sw == 0)
		{
			if (this.latch >= 0x80)
			{
				r = (byte)this.latch;
			}
			else
			{
				try
				{
					if (this.gotEOF)
					{
						r = EOF;
					}
					else if (System.in.available() > 0)
					{
						int c = System.in.read();
						if (c == '\r')
						{
							this.gotCR = true;
							r = (byte)(c | 0x80);
						}
						else if (c == '\n' && this.gotCR)
						{
							this.gotCR = false;
							r = (byte)this.latch;
						}
						else if (c == '\n')
						{
							c = '\r';
							r = (byte)(c | 0x80);
						}
						else if (c == EOF)
						{
							this.gotEOF = true;
							r = EOF;
							// if GUI, pass back 0xFF
							// TODO if CLI, exit the application
						}
						else
						{
							this.gotCR = false;
							r = (byte)(c | 0x80);
						}
					}
					else
					{
						r = (byte)this.latch;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if (sw == 1)
		{
			this.latch &= 0x7F;
			r = (byte)this.latch;
		}
		return r;
	}
}
