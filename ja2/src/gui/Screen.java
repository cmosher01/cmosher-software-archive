/*
 * Created on Nov 28, 2007
 */
package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import video.Video;

public class Screen extends JPanel
{
	private static final int FACTOR = 2;

	private Graphics2D graphics;
	private AffineTransform affine = new AffineTransform();
	{
		affine.scale(FACTOR,FACTOR);
	}

	public Screen()
	{
		setOpaque(true);
		setPreferredSize(new Dimension(Video.SIZE.width*FACTOR,Video.SIZE.height*FACTOR));
		addNotify();
	}

	public void plot(final Image image)
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					plotScreen(image);
				}
			});
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	protected void plotScreen(final Image image)
	{
		if (this.graphics == null)
		{
			this.graphics = (Graphics2D)getGraphics();
			if (this.graphics == null)
			{
				return;
			}
		}

		if (image == null)
		{
			return;
		}

		this.graphics.drawImage(image,this.affine,this);
	}

	/**
	 * @param g
	 */
	@Override
	public void paint(@SuppressWarnings("unused") Graphics g)
	{
		// we don't need to paint anything; we just let the
		// emulated screen refresh do it's thing
	}
}
