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
public class AssertionRelBinding extends TupleBinding
{
	private final AssertionIDBinding bindingAssertionID;

	/**
	 * @param bindingAssertionID 
	 */
	public AssertionRelBinding(final AssertionIDBinding bindingAssertionID)
	{
		this.bindingAssertionID = bindingAssertionID;
	}

	/**
	 * @param objectAssertionRel
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object objectAssertionRel, final TupleOutput output)
	{
		final AssertionRel assertionRel = (AssertionRel)objectAssertionRel;

		this.bindingAssertionID.objectToEntry(assertionRel.getParent(),output);
		this.bindingAssertionID.objectToEntry(assertionRel.getChild(),output);
	}

	/**
	 * @param input
	 * @return new Assertion
	 */
	@Override
	public AssertionRel entryToObject(final TupleInput input)
	{
		final AssertionID idParent = this.bindingAssertionID.entryToObject(input);
		final AssertionID idChild = this.bindingAssertionID.entryToObject(input);

		return new AssertionRel(idParent,idChild);
	}

	/**
	 * @return Returns the bindingAssertionID.
	 */
	public AssertionIDBinding getBindingAssertionID()
	{
		return this.bindingAssertionID;
	}

}
