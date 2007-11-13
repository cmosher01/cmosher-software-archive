package chipset;

import disk.DiskDriveSimple;
import video.Video;

/*
 * Created on Aug 1, 2007
 */
public class Clock
{
	volatile boolean shutdown;
	private final CPU6502 cpu;
	private final Video video;
	private final DiskDriveSimple diskDrive;

	private Thread clth;

	private long lasttime = System.currentTimeMillis();
	private long times;

	public Clock(final CPU6502 cpu, final Video video, final DiskDriveSimple diskDrive)
	{
		this.cpu = cpu;
		this.video = video;
		this.diskDrive = diskDrive;
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

	private void runth()
	{
		while (!this.shutdown)
		{
			cpu.tick();
			video.tick();

//			if (graphics && !driveMotor)
//			{
//			++this.times;
//			if (this.times >= 102273)
//			{
//				this.times = 0;
//				final long thistime = System.currentTimeMillis();
//				final long actual = thistime-this.lasttime;
//				this.lasttime = thistime;
//				final long delta = 100-actual;
//				if (false)//(delta >= 2)
//				{
//					try
//					{
//						Thread.sleep(delta);
//					}
//					catch (InterruptedException e)
//					{
//						this.shutdown = true;
//					}
//				}
//			}
//			}
		}
		cpu.stopped();
		video.stopped();
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
