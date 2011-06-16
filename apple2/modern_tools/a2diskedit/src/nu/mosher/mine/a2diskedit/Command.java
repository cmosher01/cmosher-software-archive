package nu.mosher.mine.a2diskedit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class Command implements TreeNode
{
	private static Icon mIcon = null;//new ImageIcon("d:\\temp\\blank.gif");
	private final String mName;

	public Command(String name)
	{
		mName = name;
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#getIcon()
	 */
	public Icon getIcon()
	{
		return mIcon;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer(30);
		s.append("<html><body text=\"blue\"><u>");
		s.append(mName);
		s.append("</u></body></html>");
		return s.toString();
	}

	public String getRightText()
	{
		String s = new String();
		return s;
	}

	public abstract void dispatch();
}
