package nu.mosher.mine.a2diskedit;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class A2DiskContents
{
	private final A2DiskImage image;
	public A2DiskContents(A2DiskImage img)
	{
		image = img;
	}

	public void parse(int osType)
	{
		if (osType == A2DiskImage.DOS33)
		{
		}
		else if (osType == A2DiskImage.PRODOS)
		{
		}
	}
}
