/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import java.util.Date;
import nu.mine.mosher.time.Time;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class TimeBinding extends TupleBinding
{
	@Override
	public void objectToEntry(final Object objectTime, final TupleOutput output)
	{
		final Time time = (Time)objectTime;

		output.writeLong(time.asDate().getTime());
	}

	@Override
	public Time entryToObject(final TupleInput input)
	{
		return new Time(new Date(input.readLong()));
	}
}
