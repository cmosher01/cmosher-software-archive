/*
 * Created on Dec 2, 2007
 */
package keyboard;

public class KeyboardProducer //extends KeyAdapter implements KeyListener
{
	private static final int CR = '\r';

	private final KeypressQueue keys;

	public KeyboardProducer(final KeypressQueue keys)
	{
		this.keys = keys;
	}

//	@Override
//	public void keyPressed(final KeyEvent e)
//	{
//		final int key = e.getKeyCode();
//		final char chr = e.getKeyChar();
//		final int mod = e.getModifiersEx();
//
//		if (key == KeyEvent.VK_ENTER)
//		{
//			this.keys.put(CR);
//		}
//		else if (key == KeyEvent.VK_LEFT)
//		{
//			this.keys.put(8);
//		}
//		else if (key == KeyEvent.VK_RIGHT)
//		{
//			this.keys.put(21);
//		}
//		else if (key == KeyEvent.VK_UP)
//		{
//			this.keys.put(11);
//		}
//		else if (key == KeyEvent.VK_DOWN)
//		{
//			this.keys.put(10);
//		}
//		else if ('a' <= chr && chr <= 'z')
//		{
//			this.keys.put(chr-0x20);
//		}
//		else if (chr == '@' && (mod & InputEvent.SHIFT_DOWN_MASK) != 0 && (mod & InputEvent.CTRL_DOWN_MASK) != 0 )
//		{
//			this.keys.put(0);
//		}
//		else if (0 <= chr && chr < 0x80)
//		{
//			this.keys.put(chr);
//		}
//	}
}
