/*
 * Created on Dec 3, 2007
 */
package keyboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import video.Video;

/*
 * Created on Sep 12, 2007
 */
public class VideoKeyHandler extends KeyAdapter implements KeyListener
{
	private final Video video;

	/**
	 * @param cpu
	 * @param video 
	 */
	public VideoKeyHandler(final Video video)
	{
		this.video = video;
	}

	/**
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		final int key = e.getKeyCode();
		if (key == KeyEvent.VK_F4)
		{
			this.video.toggleColorKiller();
		}
		else if (key == KeyEvent.VK_F5)
		{
			this.video.toggleColorMap();
		}
	}
}
