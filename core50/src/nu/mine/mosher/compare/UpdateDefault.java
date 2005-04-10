/*
 * Created on Apr 9, 2005
 */
package nu.mine.mosher.core;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class UpdateDefault implements Updater
{
	/**
	 * 
	 */
	public UpdateDefault()
	{
		super();
	}

	/**
	 * @param objNew
	 * @throws UpdateException
	 */
	public void insert(T objNew) throws UpdateException
	{
	}

	/**
	 * @param objOld
	 * @param objNew
	 * @throws UpdateException
	 */
	public void update(T objOld, T objNew) throws UpdateException
	{
	}

	/**
	 * @param objOld
	 * @throws UpdateException
	 */
	public void delete(T objOld) throws UpdateException
	{
	}
}
