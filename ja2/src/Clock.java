import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * Created on Aug 1, 2007
 */
public class Clock
{
	volatile boolean shutdown;
	private Thread clth;

	public interface Timed
	{
		void tick();
		void stopped();
	}

	private final List<Timed> rTimed = new ArrayList<Timed>();
	Clock(final Collection<Timed> rTimed)
	{
		this.rTimed.addAll(rTimed);
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
			for (final Timed timed : this.rTimed)
			{
				timed.tick();
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
