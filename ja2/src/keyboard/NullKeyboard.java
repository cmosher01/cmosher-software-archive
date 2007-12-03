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
}
