/*
 * Created on Nov 28, 2007
 */
package stdio;

import gui.UI;
import chipset.Card;

public class StandardIn implements Card
{
	private static final int EOF = -1;

	private final UI ui;
	private final StandardInProducer stdinprod;

	private byte latch;
	private boolean gotEOF;


	public StandardIn(final UI ui, final StandardInProducer stdinprod)
	{
		this.ui = ui;
		this.stdinprod = stdinprod;
	}

	public byte io(final int addr, @SuppressWarnings("unused") final byte data)
	{
		final int sw = addr & 0x0F;
		if (sw == 0)
		{
			if (this.latch >= 0)
			{
				if (this.gotEOF)
				{
					this.latch = -1;
				}
				else
				{
					this.stdinprod.start();
					final Byte k = this.stdinprod.getKeys().peek();
					if (k != null)
					{
						this.stdinprod.getKeys().remove();
						this.latch = k.byteValue();
						if (this.latch == EOF)
						{
							this.gotEOF = true;
							this.ui.handleStdInEOF();
						}
					}
				}
			}
		}
		else if (sw == 1)
		{
			this.latch &= 0x7F;
		}
		return this.latch;
	}
}
