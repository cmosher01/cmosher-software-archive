package nu.mosher.mine.a2diskedit;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

public class Disk implements TreeNode
{
	private final File mFile;
	private static final Icon mIcon = new ImageIcon("d:\\temp\\floppy.gif");
	private final JEditorPane viewRight = new JEditorPane();

	public Disk()
	{
		this(null);
	}

	public Disk(File file)
	{
		mFile = file;

		if (mFile != null)
		{
			FileInputStream fin = null;
			try
			{
				fin = new FileInputStream(mFile);
				byte[] = new byte[fin.available()];
				fin.read(image);
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (fin != null)
				{
					try
					{
						fin.close();
					}
					catch (Exception e)
					{
					}
				}
			}
		}

        viewRight.setEditable(false);
        viewRight.setContentType("text/html");
	}

	public String toString()
	{
		String s;

		if (mFile != null)
			s = mFile.getName();
		else
			s = "untitled";

		return s;
	}

	public Component getRightPane()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<html>Disk image file path: ");
		if (mFile != null)
		{
			sb.append(mFile.getAbsolutePath());
		}
		else
		{
			sb.append("[no file]");
		}
		sb.append("</html>");
		viewRight.setText(sb.toString());
		return viewRight;
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#getIcon()
	 */
	public Icon getIcon()
	{
		return mIcon;
	}
}
