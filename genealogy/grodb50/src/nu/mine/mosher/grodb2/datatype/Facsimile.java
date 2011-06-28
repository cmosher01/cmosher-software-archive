/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class Facsimile
{
	private final String mimetype;
	private final byte[] rawdata;

	/**
	 * @param mimetype
	 * @param rawdata
	 */
	public Facsimile(final String mimetype, final byte[] rawdata)
	{
		this.mimetype = mimetype;
		this.rawdata = new byte[rawdata.length];
		System.arraycopy(rawdata,0,this.rawdata,0,this.rawdata.length);
	}

	/**
	 * @return Returns the mime type.
	 */
	public String getMimeType()
	{
		return this.mimetype;
	}

	/**
	 * @return number of bytes in raw data
	 */
	public int getRawDataSize()
	{
		return this.rawdata.length;
	}

	/**
	 * @param fill array of bytes to copy raw data into
	 */
	public void getRawData(final byte[] fill)
	{
		System.arraycopy(this.rawdata,0,fill,0,this.rawdata.length);
	}
}
