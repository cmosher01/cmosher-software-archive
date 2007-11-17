package keyboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;



public class ClipboardHandler
{
	private final Keyboard keyboard;

	public ClipboardHandler(Keyboard keyboard)
	{
		this.keyboard = keyboard;
	}

	public void paste()
	{
		final String data = getClipboardContents();
		for (int i = 0; i < data.length(); ++i)
		{
			char c = data.charAt(i);
			if (c == '\n')
			{
				c = '\r';
			}
			this.keyboard.press(c);
		}
	}

	private static String getClipboardContents()
	{
		String data = "";
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final Transferable contents = clipboard.getContents(null);
		if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
		{
			try
			{
				data = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return data;
	}
}
