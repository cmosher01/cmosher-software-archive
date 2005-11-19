/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.date.DateRange;
import nu.mine.mosher.grodb.date.YMD;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class DatePeriodBinding extends TupleBinding
{
	@Override
	public void objectToEntry(final Object objectDatePeriod, final TupleOutput output)
	{
		final DatePeriod datePeriod = (DatePeriod)objectDatePeriod;

		writeDateRange(datePeriod.getStartDate(),output);
		writeDateRange(datePeriod.getEndDate(),output);
	}

	private static void writeDateRange(final DateRange dateRange, final TupleOutput output)
	{
		writeYMD(dateRange.getEarliest(),output);
		writeYMD(dateRange.getLatest(),output);
	}

	private static void writeYMD(final YMD ymd, final TupleOutput output)
	{
		output.writeInt(ymd.getYear());
		output.writeInt(ymd.getMonth());
		output.writeInt(ymd.getDay());
	}



	@Override
	public DatePeriod entryToObject(final TupleInput input)
	{
		final DateRange start = readDateRange(input);
		final DateRange end = readDateRange(input);

		return new DatePeriod(start,end);
	}

	private static DateRange readDateRange(final TupleInput input)
	{
		final YMD earliest = readYMD(input);
		final YMD latest = readYMD(input);

		return new DateRange(earliest,latest);
	}

	private static YMD readYMD(final TupleInput input)
	{
		final int year = input.readInt();
		final int month = input.readInt();
		final int day = input.readInt();

		return new YMD(year,month,day);
	}
}
