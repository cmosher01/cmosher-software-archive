/*
 * Created on Aug 14, 2004
 */

/**
 * @author Chris Mosher
 */
public class GDiffCopy extends GDiffCmd
{
	private final long pos;
	private final int len;
	public GDiffCopy(long pos, int len)
	{
		this.pos = pos;
		this.len = len;
	}
    public GDiffCopy(long pos, long end)
    {
        this.pos = pos;
        this.len = end-pos;
    }
	public long getPosition()
	{
		return pos;
	}
	public int getLength()
	{
		return len;
	}
    public long getLimit()
    {
        return pos+len;
    }
    public long getEnd()
    {
        return pos+len-1;
    }
    public boolean overlaps(GDiffCopy that)
    {
        return this.getPosition() <= that.getEnd() && that.getPosition() <= this.getEnd();
    }
    public GDiffCopy[] normalize(GDiffCopy that)
    {
        GDiffCopy dif1 = this;
        GDiffCopy dif2 = that;
        if (that.getPosition() < this.getPosition())
        {
            dif1 = that;
        }
        GDiffCopy[] rnorm = new GDiffCopy[3];
        GDiffCopy[0] = new GDiffCopy()
        return rnorm;
    }
}
