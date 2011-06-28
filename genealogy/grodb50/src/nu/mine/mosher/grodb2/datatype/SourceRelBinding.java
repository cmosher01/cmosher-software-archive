/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class SourceRelBinding extends TupleBinding
{
	private final SourceIDBinding bindingSourceID;

	/**
	 * @param bindingSourceID 
	 */
	public SourceRelBinding(final SourceIDBinding bindingSourceID)
	{
		this.bindingSourceID = bindingSourceID;
	}

	/**
	 * @param objectSourceRel
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object objectSourceRel, final TupleOutput output)
	{
		final SourceRel sourceRel = (SourceRel)objectSourceRel;

		this.bindingSourceID.objectToEntry(sourceRel.getParent(),output);
		this.bindingSourceID.objectToEntry(sourceRel.getChild(),output);
	}

	/**
	 * @param input
	 * @return new Source
	 */
	@Override
	public SourceRel entryToObject(final TupleInput input)
	{
		final SourceID idParent = this.bindingSourceID.entryToObject(input);
		final SourceID idChild = this.bindingSourceID.entryToObject(input);

		return new SourceRel(idParent,idChild);
	}

	/**
	 * @return Returns the bindingSourceID.
	 */
	public SourceIDBinding getBindingSourceID()
	{
		return this.bindingSourceID;
	}

}
