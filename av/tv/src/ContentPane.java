import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Created on Jan 18, 2008
 */
public class ContentPane extends JPanel
{
	private final BufferedImage image;
	private Graphics2D graphics;

	/**
	 * @param image
	 */
	public ContentPane(final BufferedImage image)
	{
		this.image = image;
		setOpaque(true);
		addNotify();
		setPreferredSize(new Dimension(this.image.getWidth(this),this.image.getHeight(this)));
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

		this.graphics.drawImage(this.image,0,0,this);
	}

	/**
	 * @param g
	 */
	@Override
	public void paint(@SuppressWarnings("unused") Graphics g)
	{
		g.drawImage(this.image,0,0,this);
		// we don't need to paint anything; we just let the
		// emulated screen refresh do it's thing
	}
}
