/*
 * Created on Jun 30, 2006
 */
package nu.mine.mosher.grodb.persist;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.date.DateRange;
import nu.mine.mosher.grodb.date.YMD;
import nu.mine.mosher.grodb.persist.key.AssertionID;
import nu.mine.mosher.grodb.persist.key.EventID;
import nu.mine.mosher.grodb.persist.key.FacsimileKey;
import nu.mine.mosher.grodb.persist.key.PersonaID;
import nu.mine.mosher.grodb.persist.key.PlaceID;
import nu.mine.mosher.grodb.persist.key.RoleID;
import nu.mine.mosher.grodb.persist.key.SourceID;
import nu.mine.mosher.grodb.persist.types.EventType;
import nu.mine.mosher.grodb.persist.types.RoleType;
import nu.mine.mosher.grodb.persist.types.Surety;
import nu.mine.mosher.time.Time;
import nu.mine.mosher.uuid.UUIDFactory;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class PhotoLoader
{
	private static final UUIDFactory uuid = new UUIDFactory();
	private static final Calendar calendar = Calendar.getInstance();

	private final EntityStore store;

	public PhotoLoader(final EntityStore store)
	{
		this.store = store;
	}

	public void load(final byte[] png, final String text, final Collection<PersonaID> rPersona, final PlaceID place, final int year, final int month) throws DatabaseException, ParseException
	{
		final PrimaryIndex<SourceID,Source> pkSource = this.store.getPrimaryIndex(SourceID.class,Source.class);
		final SourceID sour = new SourceID(uuid.createUUID());
		pkSource.putNoReturn(new Source(sour,"[Untitled photograph]","",getTime(year),"","In possession of Chris Mosher"));

		final PrimaryIndex<FacsimileKey,Facsimile> pkFax = this.store.getPrimaryIndex(FacsimileKey.class,Facsimile.class);
		pkFax.putNoReturn(new Facsimile(new FacsimileKey(sour,1),"image/png",png));

		final PrimaryIndex<SourceID,Transcript> pkTrans = this.store.getPrimaryIndex(SourceID.class,Transcript.class);
		pkTrans.putNoReturn(new Transcript(sour,text));

		final PrimaryIndex<EventID,Event> pkEvent = this.store.getPrimaryIndex(EventID.class,Event.class);
		final EventID event = new EventID(uuid.createUUID());
		pkEvent.putNoReturn(new Event(event,EventType.photograph,"",new DatePeriod(new DateRange(new YMD(year,month,0))),new Surety(10),place,new Surety(10)));

		final PrimaryIndex<RoleID,Role> pkRole = this.store.getPrimaryIndex(RoleID.class,Role.class);
		final PrimaryIndex<AssertionID,Assertion> pkAssertion = this.store.getPrimaryIndex(AssertionID.class,Assertion.class);
		for (final PersonaID persona: rPersona)
		{
			final RoleID role = new RoleID(uuid.createUUID());
			pkRole.putNoReturn(new Role(role,RoleType.subjectOfPhotograph,"",new Surety(10),persona,event));

			final AssertionID asert = new AssertionID(uuid.createUUID());
			pkAssertion.putNoReturn(new Assertion(asert,sour,true,"",role,null));
		}
	}

	private Time getTime(final int year)
	{
		calendar.clear();
		calendar.set(Calendar.YEAR,year);
		return new Time(calendar.getTime());
	}
}
