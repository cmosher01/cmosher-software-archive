/*
 * Created on Jan 18, 2008
 */
public class analogtv_yiq
{
	private final double y;
	private final double i;
	private final double q;

	public analogtv_yiq(final double y, final double i, final double q)
	{
		this.y = y < 0 ? 0 : y;
		this.i = i < 0 ? 0 : i;
		this.q = q < 0 ? 0 : q;
	}

	public double getI()
	{
		return this.i;
	}

	public double getQ()
	{
		return this.q;
	}

	public double getY()
	{
		return this.y;
	}
}
