package pom1.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Timer;
import pom1.apple1.Keyboard;
import pom1.apple1.Pia6820;

public class ClipboardHandler
{
	final Keyboard keyboard;
	long delay;

	public ClipboardHandler(Keyboard keyboard)
	{
		this.keyboard = keyboard;
		this.delay = Long.parseLong(System.getProperty("PASTE_DELAY","15"));
	}

	public void sendDataToApple1(Pia6820 pia)
	{
		if (pia.getKbdInterrups())
		{
			String data = getClipboardContents();
//			TyperTimerTask typerTask = new TyperTimerTask(data,keyboard,delay);
//			theTimer.schedule(typerTask,delay,delay);
			for (int i = 0; i < data.length(); ++i)
			{
				final char c = data.charAt(i);
				keyboard.keyTyped(c);
			}
		}
	}

	public static String getClipboardContents()
	{
		String data = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText)
		{
			try
			{
				data = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException ex)
			{
				System.out.println(ex);
			}
			catch (IOException ex)
			{
				System.out.println(ex);
			}
		}
		return data;
	}

	public void close()
	{
		theTimer.cancel();
	}

	static Timer theTimer = new Timer();
}
