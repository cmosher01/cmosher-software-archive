package chipset;
import java.util.Collection;

/*
 * Created on Aug 1, 2007
 */
public class Clock
{
	volatile boolean shutdown;
	private Thread clth;
	private final Timed[] rTimed;

	private long lasttime = System.currentTimeMillis();
	private long times;

	public interface Timed
	{
		void tick();
		void stopped();
	}

	public Clock(final Collection<Timed> rTimed)
	{
		this.rTimed = new Timed[rTimed.size()];
		rTimed.toArray(this.rTimed);
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
			for (int i = 0; i < this.rTimed.length; ++i)
			{
				this.rTimed[i].tick();
			}
//			if (graphics && !driveMotor)
//			{
			++this.times;
			if (this.times >= 102273)
			{
				this.times = 0;
				final long thistime = System.currentTimeMillis();
				final long actual = thistime-this.lasttime;
				this.lasttime = thistime;
				final long delta = 100-actual;
				if (false)//(delta >= 2)
				{
					try
					{
						Thread.sleep(delta);
					}
					catch (InterruptedException e)
					{
						this.shutdown = true;
					}
				}
			}
//			}
		}
		for (final Timed timed : this.rTimed)
		{
			timed.stopped();
		}
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
