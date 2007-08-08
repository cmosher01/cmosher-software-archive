import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * Created on Aug 1, 2007
 */
public class Clock
{
	public interface Timed
	{
		void tick();
	}

	private final List<Timed> rTimed = new ArrayList<Timed>();
	Clock(final Collection<Timed> rTimed)
	{
		this.rTimed.addAll(rTimed);
	}

	public void run()
	{
		System.out.println("clock is starting");
		int t = 17030*2;
		while (t-- > 0)
		{
			for (final Timed timed : this.rTimed)
			{
				timed.tick();
			}
		}
		System.out.println("clock is stopping");
	}
}
