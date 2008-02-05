package chipset;

import java.util.concurrent.atomic.AtomicBoolean;
import util.Util;



/*
 * Created on Aug 1, 2007
 */
public class TimingGenerator
{
	public static final int CRYSTAL_HZ = Util.divideRoundUp(315000000,22);
	public static final int CRYSTAL_CYCLES_PER_CPU_CYCLE = 14;
	public static final int EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE = 2;

	public static final int HORIZ_CYCLES = 65;
	public static final int AVG_CPU_HZ = (int)Math.rint(Math.round(((double)315000000*HORIZ_CYCLES)/(22*(CRYSTAL_CYCLES_PER_CPU_CYCLE*HORIZ_CYCLES+EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE))));
	public static final int CPU_HZ = Util.divideRoundUp(CRYSTAL_HZ,CRYSTAL_CYCLES_PER_CPU_CYCLE);

	private final AtomicBoolean shutdown = new AtomicBoolean(true);
	private Thread thread;

	private final Timable timable;
	private final Throttle throttle;



	public TimingGenerator(final Timable timable, final Throttle throttle)
	{
		this.timable = timable;
		this.throttle = throttle;
	}

	public void run()
	{
		if (this.thread != null)
		{
			throw new IllegalStateException();
		}
		synchronized (this.shutdown)
		{
			this.shutdown.set(false);
		}
		this.thread = new Thread(new Runnable()
		{
			@SuppressWarnings("synthetic-access")
			public void run()
			{
				try
				{
			    	threadProcedure();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
		this.thread.setName("User-TimingGenerator");
		this.thread.start();
	}

	private void threadProcedure()
	{
		while (!isShuttingDown())
		{
			this.timable.tick();
			this.throttle.tick();
		}
	}

	private boolean isShuttingDown()
	{
		synchronized (this.shutdown)
		{
			return this.shutdown.get();
		}
	}
	public boolean isRunning()
	{
		return this.thread != null;
	}

	public void shutdown()
	{
		if (this.thread == null)
		{
			throw new IllegalStateException();
		}
		synchronized (this.shutdown)
		{
			this.shutdown.set(true);
		}

		// TODO may need to do the join in it's own thread
		try
		{
			this.thread.join();
			this.thread = null;
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
