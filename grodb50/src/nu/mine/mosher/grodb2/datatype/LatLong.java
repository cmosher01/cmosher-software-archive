/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class LatLong
{
	private final float latitude;
	private final float longitude;
	/**
	 * @param latitude
	 * @param longitude
	 */
	public LatLong(final float latitude, final float longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return Returns the latitude.
	 */
	public float getLatitude()
	{
		return this.latitude;
	}

	/**
	 * @return Returns the longitude.
	 */
	public float getLongitude()
	{
		return this.longitude;
	}
}
