/*
 * Created on Nov 27, 2007
 */
package paddle;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import chipset.TimingGenerator;

public class Paddles implements PaddlesInterface
{
	private static final int PADDLE_COUNT = 4;
	private static final int PADDLE_CYCLES = 2805;

	private static final int REALTIME_1MS_CYCLES = TimingGenerator.CPU_HZ/1000;
	private static final int REALTIME_100US_CYCLES = 90;

	private final int[] rTick;

	public Paddles()
	{
		this.rTick = new int[PADDLE_COUNT];
	}

	public void tick()
	{
		for (int paddle = 0; paddle < PADDLE_COUNT; ++paddle)
		{
			if (this.rTick[paddle] > 0)
				--this.rTick[paddle];
		}
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

		if (isTimedOut(2))
			this.rTick[2] = REALTIME_1MS_CYCLES;
		if (isTimedOut(3))
			this.rTick[3] = REALTIME_100US_CYCLES;
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
