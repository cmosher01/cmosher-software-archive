package nu.mosher.mine.a2diskedit;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class Disk implements TreeNode
{
	private final File mFile;
	private static final Icon mIcon = new ImageIcon("d:\\temp\\floppy.gif");
	private final JEditorPane viewRight = new JEditorPane();
	private A2DiskImage img = null;
	private DefaultMutableTreeNode node = null;

	public Disk(DefaultMutableTreeNode n)
	{
		this(n,null);
	}

	public Disk(DefaultMutableTreeNode n, File file)
	{
		node = n;
		mFile = file;

		if (mFile != null)
		{
			byte[] image = null;
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
			if (image != null)
			{
				img = new A2DiskImage(image);
				List r = new ArrayList(30);
				img.getContents().getCatList(r);
				for (Iterator i = r.iterator(); i.hasNext();)
				{
					DefaultMutableTreeNode ne = new DefaultMutableTreeNode();
					CatEntry e = (CatEntry) i.next();
					e.setNode(ne);
					ne.setUserObject(e);
					A2DiskEdit.getApp().addNode(ne,node);
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
		if (img != null)
		{
			sb.append(" <br></br>Name: ");
			sb.append(img.getContents().getVolumeName());
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
