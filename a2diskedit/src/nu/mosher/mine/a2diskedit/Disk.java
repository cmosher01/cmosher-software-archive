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
	private static Icon mIcon = new ImageIcon("d:\\temp\\floppy.gif");
	private byte[] image = null;
	private final JEditorPane viewRight;

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
				image = new byte[fin.available()];
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

		viewRight = new JEditorPane();
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
