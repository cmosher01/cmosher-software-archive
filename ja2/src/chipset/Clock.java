package chipset;

import paddle.PaddlesInterface;
import chipset.cpu.CPU6502;
import util.Util;
import video.Video;



/*
 * Created on Aug 1, 2007
 */
public class Clock
{
	public static final int CRYSTAL_HZ = Util.divideRoundUp(315000000,22);
	private static final int CPU_CYCLES_PER_CRYSTAL_CYCLES = 14; // TODO account for the elongated CPU cycle
	public static final int CPU_HZ = Util.divideRoundUp(CRYSTAL_HZ,CPU_CYCLES_PER_CRYSTAL_CYCLES);

	private final CPU6502 cpu;
	private final Video video;
	private final PaddlesInterface paddles;

	private volatile boolean shutdown;
	private Thread thread;

	private final Throttle throttle;



	public Clock(final CPU6502 cpu, final Video video, final PaddlesInterface paddles, final Throttle throttle)
	{
		this.cpu = cpu;
		this.video = video;
		this.paddles = paddles;
		this.throttle = throttle;
	}

	public void run()
	{
		if (this.thread == null)
		{
			this.thread = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
				    	runth();
					}
					catch (final Throwable e)
					{
						e.printStackTrace();
					}
				}
			});
			this.thread.setName("Apple-Clock");
			this.thread.start();
		}
	}

	private void runth()
	{
		while (!this.shutdown)
		{
			/*
			 * One normal clock cycle. Let the CPU do one cycle of its
			 * calculation, then let the video display do one cycle's
			 * worth of scanning/displaying (one byte).
			 * Tick down the paddles, too.
			 */
			this.cpu.tick();
			this.video.tick();
			this.paddles.tick();

			this.throttle.throttle();
		}
	}

	public void shutdown()
	{
		this.shutdown = true;
		if (this.thread != null)
		{
			try
			{
				this.thread.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			this.thread = null;
		}
	}
}
