package pom1.gui;

import java.util.TimerTask;
import pom1.apple1.Keyboard;

class TyperTimerTask extends TimerTask
{
	private final Keyboard keyboard;
	private final String data;
	private final long delay;

	private int i;

	TyperTimerTask(String data, Keyboard keyboard, long delay)
	{
		this.data = data;
		this.keyboard = keyboard;
		this.delay = delay;
	}

	public void sendNextCharacter()
	{
		char key = data.charAt(i++);
		keyboard.keyTyped(key);
//		if (key == '\n')
//		{
///TODO sleep after CR during paste doesn't work
//			try
//			{
//				// sleep 10 times as long on an end of line 
//				// to allow for the extra processing by the emulated machine
//				Thread.sleep(10 * delay);
//			}
//			catch (InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//		}
		//screen.dispatchEvent(new KeyEvent(screen, 400, 0L, 0, 0, key));
		if (i == data.length())
		{
//			if (gui != null)
//				gui.synchronise(true);
			cancel();
		}
	}

	public void run()
	{
		sendNextCharacter();
	}
}
