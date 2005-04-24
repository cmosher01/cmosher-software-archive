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
		for (int y = 0; y < image.getHeight(); ++y)
		{
			for (int x = 0; x < image.getWidth(); ++x)
			{
				int rgb = image.getRGB(x,y);
				System.out.println(Integer.toHexString(rgb));
			}
		}
	}
}
