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
	public long getPosition()
	{
		return pos;
	}
	public int getLength()
	{
		return len;
	}
}
