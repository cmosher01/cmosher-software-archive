package pom1.gui;

import java.util.TimerTask;
import pom1.apple1.Keyboard;

class TyperTimerTask extends TimerTask
{
	private GUI gui;
	String data;
	int i;
	private long delay;

	TyperTimerTask(String data, GUI gui, long delay)
	{
		this.data = data;
		this.gui = gui;
		this.delay = delay;
	}

	public void sendNextCharacter()
	{
		char key = data.charAt(i++);
		gui.handleKeyEntry(Keyboard.translateKey(key));
		if (key == '\n')
		{
			try
			{
				// sleep 10 times as long on an end of line 
				// to allow for the extra processing by the emulated machine
				Thread.sleep(10 * delay);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		//screen.dispatchEvent(new KeyEvent(screen, 400, 0L, 0, 0, key));
		if (i == data.length())
		{
			if (gui != null)
				gui.synchronise(true);
			cancel();
		}
	}

	public void run()
	{
		sendNextCharacter();
		Thread.yield(); // give the GUI a chance to update
	}
}
