package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class Event
{
	private Place place;
	private DateRange dateStart;
	private DateRange dateEnd;
	private ItemType<EventType> type;

	private List<Relation<Event,Event,EventRelType>> rEvent = new ArrayList<Relation<Event,Event,EventRelType>>();

	public Event()
	{
	}
}
