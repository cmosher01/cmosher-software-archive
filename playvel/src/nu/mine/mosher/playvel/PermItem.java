package nu.mine.mosher.playvel;

import java.util.ArrayList;

import javax.jdo.PersistenceManager;

public interface PermItem
{
	String getName();
	String getId();
	ArrayList getAll(PersistenceManager pm);
}
