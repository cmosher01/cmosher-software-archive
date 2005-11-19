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
public class SourceRelChild implements SecondaryKeyCreator
{
	private final SourceRelBinding bindingSourceRel;

	/**
	 * @param bindingSourceRel
	 */
	public SourceRelChild(final SourceRelBinding bindingSourceRel)
	{
		this.bindingSourceRel = bindingSourceRel;
	}

	/**
	 * @param secondary
	 * @param keySourceRel
	 * @param dataEmpty
	 * @param resultSourceIDChild
	 * @return true
	 */
	public boolean createSecondaryKey(final SecondaryDatabase secondary, final DatabaseEntry keySourceRel, final DatabaseEntry dataEmpty, final DatabaseEntry resultSourceIDChild)
	{
		final SourceRel sourceRel = (SourceRel)this.bindingSourceRel.entryToObject(keySourceRel);

		this.bindingSourceRel.getBindingSourceID().objectToEntry(sourceRel.getChild(),resultSourceIDChild);

		return true;
	}
}
