package keyboard;



import chipset.Throttle;



/*
 * Created on Sep 12, 2007
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Keyboard implements KeyboardInterface
{
	private final KeypressQueue keys;
	private final Throttle throttle;
	private boolean lossless = true;

	private byte latch;

	private long lastGet = System.currentTimeMillis();
	private long cGet;



	public Keyboard(final KeypressQueue keys, final Throttle throttle)
	{
		this.keys = keys;
		this.throttle = throttle;
	}

	public void clear()
	{
		this.latch &= 0x7F;
	}

	public byte get()
	{
		waitIfTooFast();
		if (!this.lossless || this.latch >= 0)
		{
			final Byte k = this.keys.peek();
			if (k != null)
			{
				this.keys.remove();
				this.latch = k.byteValue();
			}
		}
		return this.latch;
	}

	private void waitIfTooFast()
	{
		if (this.throttle.isHyper())
		{
			return;
		}

		++this.cGet;
		if (this.cGet >= 0x100)
		{
			if (System.currentTimeMillis() - this.lastGet <= 1000)
			{
				/*
				 * Check every 256 gets to see if they are
				 * happening too fast (within one second).
				 * If so, *wait* for a key-press (but only up
				 * to 50 milliseconds).
				 */
				synchronized (this.keys)
				{
					try
					{
						this.keys.wait(50);
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
				}
			}
			this.cGet = 0;
		}
		this.lastGet = System.currentTimeMillis();
	}

	public void setLossless(final boolean lossless)
	{
		this.lossless = lossless;
	}

	public boolean isLossless()
	{
		return this.lossless;
	}
}
