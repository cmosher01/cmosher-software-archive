import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;

/*
 * Created on Apr 23, 2005
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class ScreenRand
{
	public static void main(String[] args) throws AWTException
	{
		Robot robot = new Robot();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle rect = new Rectangle(0, 0, d.width, d.height);
		BufferedImage image = robot.createScreenCapture(rect);
		int c = 0;
		int[] rb = new int[64];
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				int rgb = image.getRGB(x,y);
				rb[c] ^= rgb;
				++c;
				if (c >= rb.length)
				{
					c = 0;
				}
			}
		}
		int seed = 0;
		for (int i = 0; i < rb.length; i++)
		{
			int j = rb[i];
			seed <<= 1;
			if ((j & 0x400) == 0)
			{
				seed &= 1;
			}
		}
		System.out.println(Long.toHexString(seed));
	}
}
