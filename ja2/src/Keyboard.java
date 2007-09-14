import java.awt.Event;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * Created on Sep 12, 2007
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Keyboard extends KeyAdapter implements KeyListener
{
	volatile int latch;

	/**
	 * @param e
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{
		char key = e.getKeyChar();
//		System.out.println("raw keypress: "+Integer.toHexString(key));
		switch (key)
		{
			case '\n':
				key = '\r';
			break;
			case Event.LEFT:
				key = 8;
			break;
			case Event.RIGHT:
				key = 21;
			break;
			case Event.UP:
				key = 11;
			break;
			case Event.DOWN:
				key = 10;
			break;
			// case Event.DELETE : key = 127; break;
		}
		if (key >= 0x80) // ignore non-ASCII keypresses
		{
			return;
		}
		this.latch = key;
		this.latch &= 0x000000FF;
		this.latch |= 0x80;
	}

	public int get()
	{
		return this.latch;
	}

	public void clear()
	{
		this.latch &= 0x0000007F;
	}
}
