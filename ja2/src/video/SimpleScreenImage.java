/*
 * Created on Jan 6, 2008
 */
package video;

import java.awt.image.BufferedImage;

public class SimpleScreenImage extends BufferedImage
{
	public SimpleScreenImage()
	{
		super(Video.XSIZE,Video.YSIZE,BufferedImage.TYPE_INT_RGB);
	}
}
