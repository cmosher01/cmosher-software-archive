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
//        return "COPY "+range.getBegin()+", "+range.getLength()/*+" @<"+this.getTargetRange().getBegin()+","+this.getTargetRange().getEnd()+">"*/;
        Range trg = getTargetRange();
        StringBuffer sb = new StringBuffer(64);
        sb.append("@");
        append32(sb,range.getBegin());
        sb.append(" L");
        append32(sb,range.getLength());
        return sb.toString();
//        return "copy @"+range.getBegin()+": "+range.getLength()+"bytes";
    }
    /**
     * @param a
     */
    private void append32(StringBuffer sb, long a)
    {
        // TODO make work with longs
        appendHex(sb,(int)(a >> 24));
        appendHex(sb,(int)(a >> 16));
        appendHex(sb,(int)(a >> 8));
        appendHex(sb,(int)(a));
    }

    /**
     * @param i
     */
    private void appendHex(StringBuffer sb, int i)
    {
        char n0 = HexBuilder.nib(i & 0xF);
        i >>= 4;
        char n1 = HexBuilder.nib(i & 0xF);
        sb.append(n1);
        sb.append(n0);
    }
}
