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
public class TranscriptBinding extends TupleBinding
{
	/**
	 * @param objectTranscript
	 * @param output
	 */
	@Override
	public void objectToEntry(final Object objectTranscript, final TupleOutput output)
	{
		final Transcript transcript = (Transcript)objectTranscript;

		output.writeString(transcript.getDoc());
	}

	/**
	 * @param input
	 * @return new Transcript
	 */
	@Override
	public Transcript entryToObject(final TupleInput input)
	{
		return new Transcript(input.readString());
	}
}
