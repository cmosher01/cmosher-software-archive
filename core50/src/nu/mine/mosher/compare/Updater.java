package nu.mine.mosher.compare;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public interface Updater<T>
{
	/**
	 * @param objNew
	 * @throws UpdateException
	 */
	void insert(T objNew) throws UpdateException;
	/**
	 * @param objOld
	 * @param objNew
	 * @throws UpdateException
	 */
	void update(T objOld, T objNew) throws UpdateException;
	/**
	 * @param objOld
	 * @throws UpdateException
	 */
	void delete(T objOld) throws UpdateException;
}
