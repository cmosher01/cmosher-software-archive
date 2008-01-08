/*
 * Created on Jan 4, 2008
 */
package chipset;



import util.Util;
import video.VideoMode;



public class Throttle
{
	private static final int CHECK_EVERY = Util.divideRound(TimingGenerator.AVG_CPU_HZ,10);

	private final VideoMode video;
	private final Slots slots;
	private boolean hyper;

	private long msPrev = System.currentTimeMillis();
	private long times;

	public Throttle(final VideoMode video, final Slots slots)
	{
		this.video = video;
		this.slots = slots;
	}

	public void throttle()
	{
		/*
		 * If we are displaying graphics and the disk drive is not on,
		 * then try to slow down to real Apple ][ speed (1022727 Hz).
		 * (The theory is that this will allow games to run at real speed.)
		 * Otherwise, just run a fast as possible (except for slowing
		 * down while waiting for a key-press; see Keyboard.waitIfTooFast).
		 */
		if (!this.video.isText() && !this.slots.isAnyDiskDriveMotorOn() && !this.hyper)
		{
			/*
			 * Check every 100 milliseconds to see how far
			 * ahead we are, and sleep by the difference.
			 */
			++this.times;
			if (this.times >= CHECK_EVERY)
			{
				this.times = 0;
				final long msNow = System.currentTimeMillis();
				final long msActual = msNow-this.msPrev;
				this.msPrev = msNow;
				final long msDelta = 100-msActual;
				if (msDelta >= 2)
				{
					try
					{
						Thread.sleep(msDelta);
						this.msPrev = System.currentTimeMillis();
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
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
}
