import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import video.AnalogTV;

/*
 * Created on Jan 22, 2008
 */
public class TestKeyHandler implements KeyListener
{
	private final AnalogTV tv;
	public TestKeyHandler(AnalogTV tv)
	{
		this.tv = tv;
	}
	public void keyPressed(KeyEvent e)
	{
		final int key = e.getKeyCode();
		if (key == KeyEvent.VK_F2)
		{
			tv.dumpYs();
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
	{
	}
}
