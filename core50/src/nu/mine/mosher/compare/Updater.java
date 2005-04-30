package nu.mine.mosher.compare;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public interface Updater<T>
{
	void insert(T objNew) throws UpdateException;
	void update(T objOld, T objNew) throws UpdateException;
	void delete(T objOld) throws UpdateException;
}
