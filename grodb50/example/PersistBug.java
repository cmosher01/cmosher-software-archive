import java.io.File;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

public class PersistBug
{
	@Persistent
	static class Key
	{
		int id;
		Key() {}
		Key(int aid) { id = aid; }
	}

	@Entity
	static class Parent
	{
		@PrimaryKey
		Key pk;
		Parent() {}
		Parent(Key apk) { pk = apk; }
	}

	@Entity
	static class Child
	{
		@PrimaryKey
		Key pk;
		@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Parent.class,onRelatedEntityDelete=DeleteAction.CASCADE)
		Key fk;
		Child() {}
		Child(Key apk, Key afk) { pk = apk; fk = afk; }
	}

	/*
		Exception in thread "main" java.lang.UnsupportedOperationException: PersistBug$PK
			at com.sleepycat.persist.impl.Format.copySecKey(Format.java:652)
			at com.sleepycat.persist.impl.PersistKeyCreator.createSecondaryKey(PersistKeyCreator.java:82)
			at com.sleepycat.je.SecondaryDatabase.updateSecondary(SecondaryDatabase.java:537)
			at com.sleepycat.je.SecondaryTrigger.databaseUpdated(SecondaryTrigger.java:43)
			at com.sleepycat.je.Database.notifyTriggers(Database.java:1193)
			at com.sleepycat.je.Cursor.putInternal(Cursor.java:723)
			at com.sleepycat.je.Cursor.putNoOverwrite(Cursor.java:324)
			at com.sleepycat.persist.PrimaryIndex.put(PrimaryIndex.java:347)
			at com.sleepycat.persist.PrimaryIndex.put(PrimaryIndex.java:300)
			at PersistBug.main(PersistBug.java:76)
	 */
	public static void main(String[] args) throws DatabaseException
	{
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);
        Environment env = new Environment(new File("persistdb"),envConfig);

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);
        EntityStore store = new EntityStore(env,"persistdb",storeConfig);



//        Key pk = new Key(123);
//        PrimaryIndex<Key,Parent> parent = store.getPrimaryIndex(Key.class,Parent.class);
//        parent.put(new Parent(pk));
//
        PrimaryIndex<Key,Child> child = store.getPrimaryIndex(Key.class,Child.class);
//        child.put(new Child(new Key(567),pk));

        Child child2 = child.get(new Key(567));
        System.out.println(child2.fk.id);

        store.close();
        env.close();
	}
}
