/*
 * Created on Jan 4, 2008
 */
package chipset;



import gui.UI;
import util.Util;
import video.Video;



public class Throttle
{
	private static final int CHECK_EVERY = Util.divideRound(Clock.CPU_HZ,10);

	private final Video video;
	private final Slots slots;
	private final UI ui;

	private long msPrev = System.currentTimeMillis();
	private long times;

	public Throttle(final Video video, final Slots slots, final UI ui)
	{
		this.video = video;
		this.slots = slots;
		this.ui = ui;
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
		if (!this.video.isText() && !this.slots.isAnyDiskDriveMotorOn() && !this.ui.isHyper())
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
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
				}
			}
		}
	}
}
