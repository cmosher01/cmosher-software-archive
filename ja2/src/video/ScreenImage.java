/*
 * Created on Jan 21, 2008
 */
package video;

import java.awt.image.BufferedImage;

public class ScreenImage extends BufferedImage
{
	public ScreenImage()
	{
		super(AppleNTSC.H-AppleNTSC.PIC_START-2,VideoAddressing.VISIBLE_ROWS_PER_FIELD*2,BufferedImage.TYPE_INT_RGB);
	}
}
