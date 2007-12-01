/*
 * Created on Nov 27, 2007
 */
package keyboard;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;

public class Paddles
{
	private static final int PADDLE_COUNT = 2;
	private static final int PADDLE_CYCLES = 2805;

	private final Dimension pdls = new Dimension();
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
		setPdls();

		this.rTick[0] = this.pdls.height;
		this.rTick[1] = this.pdls.width;
	}

	private void setPdls()
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

		this.pdls.setSize(x,y);
	}

	public boolean paddleTimedOut(final int iPaddle)
	{
		if (PADDLE_COUNT <= iPaddle)
		{
			return false;
		}
		return this.rTick[iPaddle] <= 0;
	}
}
