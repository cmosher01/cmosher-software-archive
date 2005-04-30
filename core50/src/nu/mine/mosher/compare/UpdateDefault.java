/*
 * Created on Apr 9, 2005
 */
package nu.mine.mosher.compare;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public abstract class UpdateDefault<T> implements Updater<T>
{
	/**
	 * @param objOld
	 * @param objNew
	 * @throws UpdateException
	 */
	public void update(T objOld, T objNew) throws UpdateException
	{
		delete(objOld);
		insert(objNew);
	}
}
