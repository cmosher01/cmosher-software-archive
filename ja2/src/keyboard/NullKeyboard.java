/*
 * Created on Dec 1, 2007
 */
package keyboard;

public class NullKeyboard implements KeyboardInterface
{
	public void clear()
	{
		// do nothing
	}

	public byte get()
	{
		// no key is ever pressed
		return 0;
	}

	public boolean isHyperSpeed()
	{
		return true;
	}

	public boolean isPaddleButtonDown(@SuppressWarnings("unused") int paddle)
	{
		// no paddle button is ever pressed
		return false;
	}
}
