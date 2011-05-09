/*
 * Created on Jan 4, 2008
 */
package chipset;



import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;
import util.Util;



public class Throttle
{
	private static final String LOG_TAG = Throttle.class.getName();
	private static final int CHECK_EVERY_FRACT = 10;
	private static final int CHECK_EVERY_CYCLE = Util.divideRound(TimingGenerator.AVG_CPU_HZ,CHECK_EVERY_FRACT);
	private static final int EXPECTED_MS = 1000/CHECK_EVERY_FRACT;

	private AtomicBoolean suspend = new AtomicBoolean();

	private long msPrev = System.currentTimeMillis();
	private long times;

	private volatile float speedRatio;


	public void tick()
	{
		suspendIfNecessary();
		throttleIfNecessary();
	}

	private void throttleIfNecessary()
	{
		/*
		 * Check every 100 milliseconds to see how far
		 * ahead we are, and sleep by the difference.
		 */
		++this.times;
		if (this.times >= CHECK_EVERY_CYCLE)
		{
			final long msActual = System.currentTimeMillis()-this.msPrev;
			final long msDelta = EXPECTED_MS-msActual;
			this.speedRatio = (float)EXPECTED_MS/msActual;
//			System.err.println(this.speedRatio);

			//			sleep(msDelta);
			// TODO TESTING:
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			this.msPrev = System.currentTimeMillis();
			this.times = 0;
		}
	}

	private static void sleep(final long msDelta)
	{
		if (msDelta > 0)
		{
			try
			{
				Log.i(LOG_TAG, "Sleeping "+msDelta);
				Thread.sleep(msDelta);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	private void suspendIfNecessary()
	{
		// TODO need to wake up if user exits program
		synchronized (this.suspend)
		{
			while (this.suspend.get())
			{
				try
				{
					this.suspend.wait();
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public void toggleSuspend()
	{
		synchronized (this.suspend)
		{
			this.suspend.set(!this.suspend.get());
			this.suspend.notifyAll();
		}
	}

	public float getSpeedRatio()
	{
		return this.speedRatio;
	}
}
