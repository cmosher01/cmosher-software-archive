/*
 * Created on Jan 21, 2008
 */
package video;

import java.awt.image.BufferedImage;

public class TelevisionScreenImage extends BufferedImage
{
	public TelevisionScreenImage()
	{
		super(AppleNTSC.H,AppleNTSC.V * 2,BufferedImage.TYPE_INT_RGB);
	}
}
