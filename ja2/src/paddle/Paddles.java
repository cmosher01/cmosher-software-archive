/*
 * Created on Nov 27, 2007
 */
package paddle;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;

public class Paddles implements PaddlesInterface
{
	private static final int PADDLE_COUNT = 2;
	private static final int PADDLE_CYCLES = 2805;

	private final int[] rTick;

	public Paddles()
	{
		this.rTick = new int[PADDLE_COUNT];
	}

	public void tick()
	{
		// loop unrolled for speed
		--this.rTick[0];
		--this.rTick[1];
	}

	public void startTimers()
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
		double pMax = rect.getMaxX()/2;
		final int x = (int)Math.round(Math.rint((p-pMin)/(pMax-pMin)*PADDLE_CYCLES));

		p = loc.getY();
		pMin = rect.getMinY();
		pMax = rect.getMaxY()/2;
		p = pMin+pMax-p;
		final int y = (int)Math.round(Math.rint((p-pMin)/(pMax-pMin)*PADDLE_CYCLES));

		if (isTimedOut(0))
			this.rTick[0] = x;
		if (isTimedOut(1))
			this.rTick[1] = y;
	}

	public boolean isTimedOut(final int paddle)
	{
		if (paddle < 0 || PADDLE_COUNT <= paddle)
		{
			return false;
		}
		return this.rTick[paddle] <= 0;
	}
}
