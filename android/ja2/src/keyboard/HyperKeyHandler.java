/*
 * Created on Dec 3, 2007
 */
package keyboard;


/*
 * Created on Sep 12, 2007
 */
public class HyperKeyHandler //extends KeyAdapter implements KeyListener
{
	private final HyperMode hyper;
	private final KeyboardBufferMode buffered;

	/**
	 * @param cpu
	 * @param video 
	 */
	public HyperKeyHandler(final HyperMode hyper, final KeyboardBufferMode buffered)
	{
		this.hyper = hyper;
		this.buffered = buffered;
	}

	/**
	 * @param e
	 */
//	@Override
//	public void keyPressed(KeyEvent e)
//	{
//		final int key = e.getKeyCode();
//		if (key == KeyEvent.VK_F11)
//		{
//			this.hyper.toggleHyper();
//		}
//		else if (key == KeyEvent.VK_F12)
//		{
//			this.buffered.toggleBuffered();
//		}
//	}
}
