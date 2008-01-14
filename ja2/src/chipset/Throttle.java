/*
 * Created on Jan 4, 2008
 */
package chipset;



import java.util.concurrent.atomic.AtomicBoolean;
import util.Util;
import video.VideoMode;



public class Throttle
{
	private static final int CHECK_EVERY_FRACT = 10;
	private static final int CHECK_EVERY_CYCLE = Util.divideRound(TimingGenerator.AVG_CPU_HZ,CHECK_EVERY_FRACT);
	private static final int EXPECTED_MS = 1000/CHECK_EVERY_FRACT;

	private final VideoMode video;
	private final Slots slots;
	private volatile boolean hyper;
	private AtomicBoolean suspend = new AtomicBoolean();

	private long msPrev = System.currentTimeMillis();
	private long times;

	private long then;
	private long t;

	public Throttle(final VideoMode video, final Slots slots)
	{
		this.video = video;
		this.slots = slots;
	}

	public void tick()
	{
//		checkSpeed();
		suspendIfNecessary();
		throttleIfNecessary();
	}

	private void checkSpeed()
	{
		++this.t;
		final long now = System.currentTimeMillis();
		final long deltaMS = now - this.then;
		if (deltaMS >= 10000)
		{
			final double Hz = this.t/(deltaMS/1000.0);
			System.out.println(""+(long)Hz+" Hz");
			this.then = System.currentTimeMillis();
			this.t = 0;
		}
	}

	private void throttleIfNecessary()
	{
		/*
		 * If we are displaying graphics and the disk drive is not on,
		 * then try to slow down to real Apple ][ speed (1020484 Hz).
		 * (The theory is that this will allow games to run at real speed.)
		 * Otherwise, just run a fast as possible (except for slowing
		 * down while waiting for a key-press; see Keyboard.waitIfTooFast).
		 */
//		if (!this.video.isText() && !this.slots.isAnyDiskDriveMotorOn() && !this.hyper)
		{
			/*
			 * Check every 100 milliseconds to see how far
			 * ahead we are, and sleep by the difference.
			 */
			++this.times;
			if (this.times >= CHECK_EVERY_CYCLE)
			{
				this.times = 0;
				final long msNow = System.currentTimeMillis();
				final long msActual = msNow-this.msPrev;
				final long msDelta = EXPECTED_MS-msActual;
				sleep(msDelta);
				this.msPrev = System.currentTimeMillis();
			}
		}
	}

	private static void sleep(final long msDelta)
	{
		if (msDelta >= 2)
		{
			System.out.println("s "+msDelta);
			try
			{
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

	public boolean isHyper()
	{
		return this.hyper;
	}

	public void setHyper(boolean isHyper)
	{
			this.hyper = isHyper;
	}

	public void toggleHyper()
	{
		this.hyper = !this.hyper;
	}

	public void toggleSuspend()
	{
		synchronized (this.suspend)
		{
			this.suspend.set(!this.suspend.get());
			this.suspend.notifyAll();
		}
	}
}
