/*
 * Created on Nov 28, 2007
 */
package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import video.ScreenImage;



public class Screen extends JPanel
{
	private static final int FACTOR = 1;

	private final ScreenImage image;
	private final AffineTransform affine = new AffineTransform();

	private Graphics2D graphics;

	public Screen(final ScreenImage screenImage)
	{
		this.image = screenImage;
		this.affine.scale(FACTOR,FACTOR);
		setOpaque(true);
		setPreferredSize(new Dimension(this.image.getWidth()*FACTOR,this.image.getHeight()*FACTOR));
		addNotify();
	}

	public void plot()
	{
		try
		{
			if (SwingUtilities.isEventDispatchThread())
			{
				plotScreen();
			}
			else
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						plotScreen();
					}
				});
			}
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

	protected void plotScreen()
	{
		if (this.graphics == null)
		{
			this.graphics = (Graphics2D)getGraphics();
			if (this.graphics == null)
			{
				return;
			}
		}

		this.image.drawOnto(this.graphics,this.affine);
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
