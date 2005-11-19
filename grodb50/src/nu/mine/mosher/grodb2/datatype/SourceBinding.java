/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.time.Time;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class SourceBinding extends TupleBinding
{
	private final TimeBinding bindingTime;
	private final DatePeriodBinding bindingDatePeriod;
//	private static PlaceBinding bindingPlace = new PlaceBinding();

	/**
	 * @param period
	 * @param time
	 */
	public SourceBinding(final DatePeriodBinding period, final TimeBinding time)
	{
		this.bindingDatePeriod = period;
		this.bindingTime = time;
	}

	/**
	 * @param objectSource
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object objectSource, final TupleOutput output)
	{
		final Source source = (Source)objectSource;

		output.writeString(source.getTitle());
		output.writeString(source.getAuthor());
		this.bindingTime.objectToEntry(source.getDateWritten(),output);
		output.writeString(source.getPlaceWritten());
		output.writeString(source.getPublication());
		this.bindingDatePeriod.objectToEntry(source.getDateTopic(),output);
		//place

//		final Set<SourceID> setParent = source.getParents();
//		output.writeInt(setParent.size());
//		for (final SourceID sourceID : setParent)
//		{
//			this.bindingSourceID.objectToEntry(sourceID,output);
//		}
	}

	/**
	 * @param input
	 * @return new Source
	 */
	@Override
	public Source entryToObject(final TupleInput input)
	{
		final String title = input.readString();
		final String author = input.readString();
		final Time dateWritten = this.bindingTime.entryToObject(input);
		final String placeWritten = input.readString();
		final String publication = input.readString();
		final DatePeriod dateTopic = this.bindingDatePeriod.entryToObject(input);
//		final Place placeTopic = bindingPlace.entryToObject(input);

//		final int cParent = input.readInt();
//		final Set<SourceID> setParent = new HashSet<SourceID>(cParent);
//		for (int iParent = 0; iParent < cParent; ++iParent)
//		{
//			final SourceID sourceID = this.bindingSourceID.entryToObject(input);
//			setParent.add(sourceID);
//		}

		return new Source(/*id,*/title,author,dateWritten,placeWritten,publication,dateTopic/*,placeTopic*/);
	}
}
