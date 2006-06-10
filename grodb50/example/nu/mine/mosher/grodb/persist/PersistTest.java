/*
 * Created on May 21, 2006
 */
package nu.mine.mosher.grodb.persist;

import java.io.File;
import java.text.ParseException;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.date.DateRange;
import nu.mine.mosher.grodb.date.YMD;
import nu.mine.mosher.grodb.persist.key.AssertionID;
import nu.mine.mosher.grodb.persist.key.EventID;
import nu.mine.mosher.grodb.persist.key.PersonaID;
import nu.mine.mosher.grodb.persist.key.PlaceID;
import nu.mine.mosher.grodb.persist.key.RoleID;
import nu.mine.mosher.grodb.persist.key.SearchID;
import nu.mine.mosher.grodb.persist.key.SourceID;
import nu.mine.mosher.grodb.persist.proxy.DatePeriodProxy;
import nu.mine.mosher.grodb.persist.proxy.DateRangeProxy;
import nu.mine.mosher.grodb.persist.proxy.EnumProxy;
import nu.mine.mosher.grodb.persist.proxy.TimeProxy;
import nu.mine.mosher.grodb.persist.proxy.UUIDProxy;
import nu.mine.mosher.grodb.persist.proxy.YMDProxy;
import nu.mine.mosher.grodb.persist.types.EventType;
import nu.mine.mosher.grodb.persist.types.LatLong;
import nu.mine.mosher.grodb.persist.types.RoleType;
import nu.mine.mosher.grodb.persist.types.Surety;
import nu.mine.mosher.time.Time;
import nu.mine.mosher.uuid.UUIDFactory;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.AnnotationModel;
import com.sleepycat.persist.model.EntityModel;

public class PersistTest
{
	/**
	 * @param args
	 * @throws DatabaseException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws DatabaseException, ParseException
	{
        final EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);
        final Environment env = new Environment(new File("persistdb"),envConfig);

        final EntityModel model = new AnnotationModel();
        model.registerClass(UUIDProxy.class);
        model.registerClass(TimeProxy.class);
        model.registerClass(YMDProxy.class);
        model.registerClass(DateRangeProxy.class);
        model.registerClass(DatePeriodProxy.class);
        model.registerClass(EnumProxy.class);

        final StoreConfig storeConfig = new StoreConfig();
        storeConfig.setModel(model);
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);
        final EntityStore store = new EntityStore(env,"persistdb",storeConfig);

        final UUIDFactory uuid = new UUIDFactory();

        final PrimaryIndex<SourceID,Source> pkSource = store.getPrimaryIndex(SourceID.class,Source.class);
        final SourceID sour1 = new SourceID(uuid.createUUID());
        pkSource.putNoReturn(new Source(sour1,"US Census, 1860","US Census Bureau",Time.readFromString("1860-06-01T00:00:00.000-0005"),"US","1860"));

        final PrimaryIndex<PersonaID,Persona> pkPersona = store.getPrimaryIndex(PersonaID.class,Persona.class);
        final PersonaID john1 = new PersonaID(uuid.createUUID());
        pkPersona.putNoReturn(new Persona(john1,"John /Smith/"));

        final PrimaryIndex<PlaceID,Place> pkPlace = store.getPrimaryIndex(PlaceID.class,Place.class);
        final PlaceID anytown = new PlaceID(uuid.createUUID());
        pkPlace.putNoReturn(new Place(anytown,"Anytown, USA","Anytown","Anytown","Anyt.","",new LatLong(73.023f,28.31f)));

        final PrimaryIndex<EventID,Event> pkEvent = store.getPrimaryIndex(EventID.class,Event.class);
        final EventID census1860Anytown = new EventID(uuid.createUUID());
        pkEvent.putNoReturn(new Event(census1860Anytown,EventType.census,"",new DatePeriod(new DateRange(new YMD(1860,6,1))),new Surety(10),anytown,new Surety(10)));

        final PrimaryIndex<RoleID,Role> pkRole = store.getPrimaryIndex(RoleID.class,Role.class);
        final RoleID role1 = new RoleID(uuid.createUUID());
        pkRole.putNoReturn(new Role(role1,RoleType.enumeratee,"",new Surety(10),john1,census1860Anytown));

        final PrimaryIndex<AssertionID,Assertion> pkAssertion = store.getPrimaryIndex(AssertionID.class,Assertion.class);
        final AssertionID assert1 = new AssertionID(uuid.createUUID());
        pkAssertion.putNoReturn(new Assertion(assert1,sour1,true,"",role1,null,null,null));

        final SourceID sour2 = new SourceID(uuid.createUUID());
        pkSource.putNoReturn(new Source(sour2,"US Census, 1850","US Census Bureau",Time.readFromString("1850-06-01T00:00:00.000-0005"),"US","1850"));

        final PrimaryIndex<SearchID,Search> pkSearch = store.getPrimaryIndex(SearchID.class,Search.class);
        final SearchID search1 = new SearchID(uuid.createUUID());
        pkSearch.putNoReturn(new Search(search1,"Found him in 1860 census, so I need to check for him in 1850 census, too",sour2,assert1));
	}
}
