/*
 * Created on Jan 21, 2008
 */
package video;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import javax.imageio.ImageIO;
import display.AppleNTSC;

public class ScreenImage extends Observable
{
	private final BufferedImage image;
	private final DataBuffer imageBuf;

	public ScreenImage()
	{
		this.image = new BufferedImage(AppleNTSC.H-AppleNTSC.PIC_START-2,VideoAddressing.VISIBLE_ROWS_PER_FIELD*2,BufferedImage.TYPE_INT_RGB);
		this.imageBuf = image.getRaster().getDataBuffer();
	}

	@Override
	public void notifyObservers()
	{
		setChanged();
		super.notifyObservers();
	}

	public void setElem(final int i, final int val)
	{
		this.imageBuf.setElem(i,val);
	}

	public void setAllElem(final int v)
	{
		final int n = this.imageBuf.getSize();
		for (int i = 0; i < n; ++i)
		{
			this.imageBuf.setElem(i,v);
		}
	}

	public int getWidth()
	{
		return this.image.getWidth();
	}

	public int getHeight()
	{
		return this.image.getHeight();
	}

	public BufferedImage getImage()
	{
		return this.image;
	}

	private static final ImageObserver nullImageObserver = new ImageObserver()
	{
		@SuppressWarnings("unused")
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
		{
			return false;
		}
	};

	public void drawOnto(final Graphics2D graphics, final AffineTransform affine)
	{
		graphics.drawImage(this.image,affine,ScreenImage.nullImageObserver);
	}

	public void dump(final String type, final File file) throws IOException
	{
		ImageIO.write(this.image,type,file);
	}
}
