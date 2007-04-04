/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb.persist.key;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class FacsimileKey
{
	private final SourceID idSource;
	private final int seqDisplay;

	/**
	 * @param idSource
	 * @param display
	 */
	public FacsimileKey(final SourceID idSource, final int display)
	{
		this.idSource = idSource;
		this.seqDisplay = display;
	}

	/**
	 * @return Returns the idSource.
	 */
	public SourceID getSource()
	{
		return this.idSource;
	}

	/**
	 * @return Returns the seqDisplay.
	 */
	public int getDisplaySequence()
	{
		return this.seqDisplay;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof FacsimileKey))
		{
			return false;
		}
		final FacsimileKey that = (FacsimileKey)object;
		return this.idSource.equals(that.idSource) && this.seqDisplay == that.seqDisplay;
	}

	@Override
	public int hashCode()
	{
		return this.idSource.hashCode() ^ this.seqDisplay;
	}
}
