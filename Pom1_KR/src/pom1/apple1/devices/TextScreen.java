/*
 * Created on Aug 27, 2007
 */
package pom1.apple1.devices;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class TextScreen extends JTextPane implements OutputDevice
{
	public TextScreen()
	{
		reset();
		setEditable(false);
        final StyledDocument doc = getStyledDocument();
        final Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        doc.addStyle("regular",def);
		StyleConstants.setFontFamily(def,"Monospaced");
		StyleConstants.setFontSize(def,10);
	}

	public void putCharacter(int c)
	{
		if (c == 0x0D)
		{
			c = '\n';
		}
		append((char)c);
//		try
//		{
//			SwingUtilities.invokeAndWait(append);
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
//		catch (InvocationTargetException e)
//		{
//			e.printStackTrace();
//		}
	}
	private final Append append = new Append();
	class Append implements Runnable
	{
		public char c;
		public void run()
		{
			append(c);
		}
	};
	private void append(char c)
	{
		char[] rc = { (char)c };
		final StyledDocument doc = getStyledDocument();
		try
		{
			doc.insertString(doc.getLength(),new String(rc),doc.getStyle("regular"));
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		revalidate();
		//repaint();
//		this.tp.replaceSelection(new String(rc));
		setCaretPosition(doc.getLength());
	}

	public void reset()
	{
		final StyledDocument doc = getStyledDocument();
		try
		{
			doc.remove(0,doc.getLength());
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		revalidate();
		setPreferredSize(new Dimension(256,512));
	}
}
