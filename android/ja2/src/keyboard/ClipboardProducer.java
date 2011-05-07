package keyboard;

import java.io.IOException;



public class ClipboardProducer //extends KeyAdapter implements KeyListener
{
	private static final int CR = '\r';
	private static final int LF = '\n';

	private final KeypressQueue keys;

	public ClipboardProducer(final KeypressQueue keys)
	{
		this.keys = keys;
	}

//	@Override
//	public void keyPressed(KeyEvent e)
//	{
//		final int key = e.getKeyCode();
//		if (key == KeyEvent.VK_INSERT)
//		{
//			paste();
//		}
//	}

	private void paste()
	{
		final String data = getClipboardContents();
		for (int i = 0; i < data.length(); ++i)
		{
			char c = data.charAt(i);
			if (c == LF)
			{
				c = CR;
			}
			this.keys.put(c);
		}
	}

	private static String getClipboardContents()
	{
		String data = "";
//		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		final Transferable contents = clipboard.getContents(null);
//		if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
//		{
//			try
//			{
//				data = (String)contents.getTransferData(DataFlavor.stringFlavor);
//				if (data == null)
//				{
//					data = "";
//				}
//			}
//			catch (UnsupportedFlavorException ex)
//			{
//				ex.printStackTrace();
//			}
//			catch (IOException ex)
//			{
//				ex.printStackTrace();
//			}
//		}
		return data;
	}
}
