/*
 * Created on Nov 28, 2007
 */
package stdio;

import keyboard.KeypressQueue;
import gui.UI;
import chipset.Card;

public class StandardIn extends Card
{
	public interface EOFHandler
	{
		void handleEOF();
	}

	private static final int EOF = -1;

	private final EOFHandler eofHandler;
	private final KeypressQueue stdinkeys;

	private byte latch;
	private boolean gotEOF;


	public StandardIn(final EOFHandler eofHandler, final KeypressQueue stdinkeys)
	{
		this.eofHandler = eofHandler;
		this.stdinkeys = stdinkeys;
	}

	@Override
	public byte io(final int addr, @SuppressWarnings("unused") final byte data, @SuppressWarnings("unused") final boolean writing)
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
					final Byte k = this.stdinkeys.peek();
					if (k != null)
					{
						this.stdinkeys.remove();
						this.latch = k.byteValue();
						if (this.latch == EOF)
						{
							this.gotEOF = true;
							this.eofHandler.handleEOF();
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

	/**
	 * @return
	 */
	@Override
	public String getTypeName()
	{
		return "IN#2 reads from standard-in";
	}
}
