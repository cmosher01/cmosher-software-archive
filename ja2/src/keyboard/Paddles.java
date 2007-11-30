/*
 * Created on Nov 27, 2007
 */
package keyboard;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;

public class Paddles
{
	private static final int PADDLE_CYCLES = 2805;

	private final int[] rTick;

	public Paddles(final int cPaddle)
	{
		this.rTick = new int[cPaddle];
	}

	public void tick()
	{
		for (int it = 0; it < this.rTick.length; ++it)
		{
			--this.rTick[it];
		}
	}

	public void startPaddleTimers()
	{
		try
		{
			tryStartPaddleTimers();
		}
		catch (final Throwable t)
		{
			t.printStackTrace();
		}
	}

	private void tryStartPaddleTimers()
	{
		final PointerInfo mouse = MouseInfo.getPointerInfo();
		final Rectangle rect = mouse.getDevice().getDefaultConfiguration().getBounds();
		final Point loc = mouse.getLocation();

		double p = loc.getX();
		double pMin = rect.getMinX();
		double pMax = rect.getMaxX();
		this.rTick[1] = (int)Math.round(Math.rint((p-pMin)/(pMax-pMin)*PADDLE_CYCLES));

		p = loc.getY();
		pMin = rect.getMinY();
		pMax = rect.getMaxY();
		p = pMin+pMax-p;
		this.rTick[0] = (int)Math.round(Math.rint((p-pMin)/(pMax-pMin)*PADDLE_CYCLES));
	}

	public boolean paddleTimedOut(final int iPaddle)
	{
		return this.rTick[iPaddle] <= 0;
	}
}
