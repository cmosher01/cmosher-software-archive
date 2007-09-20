import java.util.Collection;

/*
 * Created on Aug 1, 2007
 */
public class Clock
{
	volatile boolean shutdown;
	private Thread clth;
	private final Timed[] rTimed;

	public interface Timed
	{
		void tick();
		void stopped();
	}

	Clock(final Collection<Timed> rTimed)
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
		System.out.println("clock is starting");
		while (!this.shutdown)
		{
			for (int i = 0; i < this.rTimed.length; ++i)
			{
				this.rTimed[i].tick();
			}
		}
		System.out.println("clock is stopping");
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
