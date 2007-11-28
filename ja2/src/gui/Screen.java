/*
 * Created on Nov 28, 2007
 */
package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import video.Video;

public class Screen extends JPanel
{
	public Screen()
	{
		setOpaque(true);
		setPreferredSize(new Dimension(Video.SIZE.width,Video.SIZE.height));
		addNotify();
	}

	public void plot(final Image image)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				plotScreen(image);
			}
		});
	}

	private Graphics graphics;
	protected void plotScreen(final Image image)
	{
		if (this.graphics == null)
		{
			this.graphics = getGraphics();
			if (this.graphics == null)
			{
				return;
			}
		}

		if (image == null)
		{
			return;
		}

		this.graphics.drawImage(image,0,0,this);
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
