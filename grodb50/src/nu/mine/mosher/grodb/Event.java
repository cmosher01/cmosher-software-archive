package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;
import nu.mine.mosher.grodb.date.DateRange;

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
	private String notes;

	private List<Relation<Event,Event,EventRelType>> rEventUp = new ArrayList<Relation<Event,Event,EventRelType>>();
	private List<Relation<Event,Event,EventRelType>> rEventDown = new ArrayList<Relation<Event,Event,EventRelType>>();
	
	public Event()
	{
	}
}
