package nu.mine.mosher;
/*
 * Created on Aug 14, 2004
 */

/**
 * @author Chris Mosher
 */
public class GDiffCopy extends GDiffCmd
{
	private final Range range;
	public GDiffCopy(Range range)
	{
		this.range = range;
	}
	public Range getRange()
	{
		return range;
	}
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "COPY "+range.getBegin()+", "+range.getLength()+" @<"+this.getTargetRange().getBegin()+","+this.getTargetRange().getEnd()+">";
    }
}
