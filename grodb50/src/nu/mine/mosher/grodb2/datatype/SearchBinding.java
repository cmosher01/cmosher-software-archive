/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class SearchBinding extends TupleBinding
{
	private final SourceIDBinding bindingSourceID;
	private final AssertionIDBinding bindingAssertionID;

	/**
	 * @param bindingSourceID
	 * @param bindingAssertionID
	 */
	public SearchBinding(final SourceIDBinding bindingSourceID, final AssertionIDBinding bindingAssertionID)
	{
		this.bindingSourceID = bindingSourceID;
		this.bindingAssertionID = bindingAssertionID;
	}

	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final Search search = (Search)object;

		output.writeString(search.getNotes());
		this.bindingSourceID.objectToEntry(search.getSource(),output);
		this.bindingAssertionID.objectToEntry(search.getAssertion(),output);
	}

	@Override
	public Search entryToObject(final TupleInput input)
	{
		final String notes = input.readString();
		final SourceID source = this.bindingSourceID.entryToObject(input);
		final AssertionID assertion = this.bindingAssertionID.entryToObject(input);

		return new Search(notes,source,assertion);
	}
}
