package nu.mosher.mine.a2diskedit;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Disk implements TreeNode
{
	final private File mFile;
	private static Icon mIcon = new ImageIcon("d:\\temp\\floppy.gif");
	byte[] image = null;
	JScrollPane mPane = null;
	JHexEdit

	public Disk()
	{
		this(null);
	}

	public Disk(File file)
	{
		mFile = file;

		JHexEdit he = null;
		if (mFile != null)
		{
			try
			{
				he = new JHexEdit(mFile);
				image = he.image();
			}
			catch (IOException e)
			{
				he = null;
			}
		}
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

	public JScrollPane getRightPane()
	{
		if (mPane == null)
		{
			mPane = new JScrollPane(he);
		}

		return mPane;
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#getIcon()
	 */
	public Icon getIcon()
	{
		return mIcon;
	}
}
