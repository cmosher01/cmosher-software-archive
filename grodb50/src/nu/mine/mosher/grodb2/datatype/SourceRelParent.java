/*
 * Created on Nov 15, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class SourceRelParent implements SecondaryKeyCreator
{
	private final SourceRelBinding bindingSourceRel;

	/**
	 * @param bindingSourceRel
	 */
	public SourceRelParent(final SourceRelBinding bindingSourceRel)
	{
		this.bindingSourceRel = bindingSourceRel;
	}

	/**
	 * @param secondary
	 * @param keySourceRel
	 * @param dataEmpty
	 * @param resultSourceIDParent
	 * @return true
	 */
	public boolean createSecondaryKey(final SecondaryDatabase secondary, final DatabaseEntry keySourceRel, final DatabaseEntry dataEmpty, final DatabaseEntry resultSourceIDParent)
	{
		final SourceRel sourceRel = (SourceRel)this.bindingSourceRel.entryToObject(keySourceRel);

		this.bindingSourceRel.getBindingSourceID().objectToEntry(sourceRel.getParent(),resultSourceIDParent);

		return true;
	}
}
