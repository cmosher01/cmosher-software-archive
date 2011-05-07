/*
 * Created on Dec 2, 2007
 */

// TODO ANDROID implement paddle buttons
package paddle;

//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;

public class PaddleButtons //extends KeyAdapter implements KeyListener
{
	private final PaddleButtonStates states;

	public PaddleButtons(final PaddleButtonStates states)
	{
		this.states = states;
	}

//	@Override
//	public void keyPressed(final KeyEvent e)
//	{
//		final int key = e.getKeyCode();
//
//		if (key == KeyEvent.VK_F6)
//		{
//			this.states.setButton(0,true);
//		}
//		else if (key == KeyEvent.VK_F7)
//		{
//			this.states.setButton(1,true);
//		}
//		else if (key == KeyEvent.VK_F8)
//		{
//			this.states.setButton(2,true);
//		}
//	}
//
//	@Override
//	public void keyReleased(KeyEvent e)
//	{
//		final int key = e.getKeyCode();
//		if (key == KeyEvent.VK_F6)
//		{
//			this.states.setButton(0,false);
//		}
//		else if (key == KeyEvent.VK_F7)
//		{
//			this.states.setButton(1,false);
//		}
//		else if (key == KeyEvent.VK_F8)
//		{
//			this.states.setButton(2,false);
//		}
//	}
}
