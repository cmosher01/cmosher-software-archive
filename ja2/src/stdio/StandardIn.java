/*
 * Created on Nov 28, 2007
 */
package stdio;

import gui.UI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import chipset.Card;

public class StandardIn implements Card
{
	private static final int EOF = -1;

	private final UI ui;
	private final BlockingQueue<Integer> qKeys = new LinkedBlockingQueue<Integer>();
	private StandardInProducer in;

	private byte latch;
	private boolean gotEOF;


	public StandardIn(final UI ui)
	{
		this.ui = ui;
	}

	public byte io(final int addr, @SuppressWarnings("unused") final byte data)
	{
		if (this.in == null)
		{
			this.in = new StandardInProducer(this.qKeys);
		}
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
					final Integer k = this.qKeys.peek();
					if (k != null)
					{
						this.qKeys.remove();
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
