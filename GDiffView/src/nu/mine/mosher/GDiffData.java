package nu.mine.mosher;
/*
 * Created on Aug 14, 2004
 */

/**
 * @author Chris
 */
public class GDiffData extends GDiffCmd
{
	private final byte[] data;
    private Range trg;

    /**
	 * @param data
	 */
	public GDiffData(final byte[] data)
	{
		this.data = data;
	}
	
	/**
	 * @return Returns the data.
	 */
	public byte[] getData()
	{
		return data;
	}

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "DATA ["+data.length+" bytes]";
    }

    public void setTargetRange(Range r)
    {
        trg = r;
    }

    /**
     * @return Returns the trg.
     */
    public Range getTargetRange()
    {
        return trg;
    }
}
