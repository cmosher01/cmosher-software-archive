/*
 * Created on Nov 13, 2005
 */
import java.util.ArrayList;
import java.util.List;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;



/**
 * TODO
 *
 * @author Chris Mosher
 */
public class IntListBinding extends TupleBinding
{
	/**
	 * @param object
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object object, final TupleOutput output)
	{
		final List<Integer> rInt = (List<Integer>)object;
		output.writeInt(rInt.size());
		for (final int i: rInt)
		{
			output.writeInt(i);
		}
	}

	/**
	 * @param input
	 * @return
	 */
	@Override
	public ArrayList<Integer> entryToObject(final TupleInput input)
	{
		final int siz = input.readInt();
		final ArrayList<Integer> rInt = new ArrayList<Integer>(siz);
		for (int i = 0; i < siz; ++i)
		{
			final int intIn = input.readInt();
			rInt.add(intIn);
		}
		return rInt;
	}
}
