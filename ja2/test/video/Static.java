/*
 * Created on Jan 24, 2008
 */
package video;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Static
{
	static AnalogTV tv = new AnalogTV();
	static JFrame frame;
	static Thread upd;
	static BufferedImage image = new BufferedImage(AppleNTSC.H,AppleNTSC.V * 2,BufferedImage.TYPE_INT_RGB);
	static Random rand = new Random();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException, InvocationTargetException
	{
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				Static x = new Static();
				x.run();
				upd = new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							while (true)
								update();
						}
						catch (Throwable e)
						{
							e.printStackTrace();
						}
					}
				});
				upd.setDaemon(true);
				upd.start();
			}
		});
	}

	void run()
	{
        frame = new JFrame();

        // Set the window's size and position.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new Dimension(AppleNTSC.H,AppleNTSC.V*2));

        // Display the window.
        frame.setVisible(true);
	}

	static ImageObserver obs = new ImageObserver()
	{
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
		{
			return false;
		}
	};

	static void update() throws InterruptedException, InvocationTargetException
	{
		makeimage();
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				Container c = frame.getContentPane();
				Graphics graphics = c.getGraphics();
				graphics.drawImage(image,0,0,obs);
			}
		});
	}

	private static void makeimage()
	{
		tv.isig = 0;
		for (int i = 0; i < AppleNTSC.H*AppleNTSC.V; ++i)
		{
			tv.write_signal(rand.nextInt(140)-40);
		}
		tv.draw_signal(image);
	}
}
