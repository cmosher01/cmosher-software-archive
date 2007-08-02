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
		rTimed.addAll(rTimed);
	}

	public void run()
	{
		for (final Timed timed : this.rTimed)
		{
			timed.tick();
		}
	}
}
