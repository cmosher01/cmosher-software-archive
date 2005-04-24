import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
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
		robot.createScreenCapture(regdesktopion);
	}
}
