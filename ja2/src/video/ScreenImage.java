/*
 * Created on Jan 6, 2008
 */
package video;

import java.awt.image.BufferedImage;

public class ScreenImage extends BufferedImage
{
	public ScreenImage()
	{
		super(Video.XSIZE,Video.YSIZE,BufferedImage.TYPE_INT_RGB);
	}
}
