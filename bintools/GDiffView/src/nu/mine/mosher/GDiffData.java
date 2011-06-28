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
//        return "DATA ["+data.length+" bytes]"/*+" @<"+this.getTargetRange().getBegin()+","+this.getTargetRange().getEnd()+">"*/;
        Range trg = getTargetRange();
        return "data ["+data.length+" bytes] to ["+trg.getBegin()+"-"+trg.getEnd()+"]";
    }
}
