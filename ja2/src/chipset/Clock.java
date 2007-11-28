package chipset;

import keyboard.Paddles;
import disk.DiskDriveSimple;
import util.Util;
import video.Video;

/*
 * Created on Aug 1, 2007
 */
public class Clock
{
	private static final int CRYSTAL_HZ = Util.divideRoundUp(315000000,22);
	private static final int CPU_CYCLES_PER_CRYSTAL_CYCLES = 14;
	public static final int CPU_HZ = Util.divideRoundUp(CRYSTAL_HZ,CPU_CYCLES_PER_CRYSTAL_CYCLES);

	volatile boolean shutdown;
	private final CPU6502 cpu;
	private final Video video;
	private final DiskDriveSimple diskDrive;
	private final Paddles paddles;

	private Thread clth;

	private long msPrev = System.currentTimeMillis();
	private long times;

	public Clock(final CPU6502 cpu, final Video video, final DiskDriveSimple diskDrive, final Paddles paddles)
	{
		this.cpu = cpu;
		this.video = video;
		this.diskDrive = diskDrive;
		this.paddles = paddles;
	}

	public void run()
	{
		if (this.clth == null)
		{
			this.clth = new Thread(new Runnable()
			{
				public void run()
				{
			    	runth();
				}
			});
			this.clth.start();
		}
	}

	private static final int CHECK_EVERY = Util.divideRound(CPU_HZ,10);

	void runth()
	{
		while (!this.shutdown)
		{
			/*
			 * One normal clock cycle. Let the CPU do one cycle of its
			 * calculation, then let the video display do one cycle's
			 * worth of scanning/displaying (one byte).
			 */
			this.cpu.tick();
			this.video.tick();
			this.paddles.tick();

			/*
			 * If we are displaying graphics and the disk drive is not on,
			 * then try to slow down to real Apple ][ speed (1022727 Hz).
			 * (The theory is that this will allow games to run at real speed.)
			 * Otherwise, just run a fast as possible (except for slowing
			 * down while waiting for a key-press; see Keyboard.waitIfTooFast).
			 */
			if (!this.video.isText() && !this.diskDrive.isMotorOn())
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
							this.shutdown = true;
						}
					}
				}
			}
		}
		this.cpu.stopped();
		this.video.stopped();
	}

	public void shutdown()
	{
		this.shutdown = true;
		if (this.clth != null)
		{
			try
			{
				this.clth.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
