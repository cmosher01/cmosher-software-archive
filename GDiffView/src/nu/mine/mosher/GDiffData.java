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
}
