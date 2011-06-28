/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class EnumBinding<E extends Enum<E>> extends TupleBinding
{
	private final Class<E> ec;

	/**
	 * @param ec
	 */
	public EnumBinding(final Class<E> ec)
	{
		this.ec = ec;
	}

	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final E type = this.ec.cast(object);

		output.writeString(type.name());
	}

	@Override
	public E entryToObject(final TupleInput input)
	{
		final String name = input.readString();

		return Enum.<E>valueOf(this.ec,name);
	}
}
