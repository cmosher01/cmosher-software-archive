package nu.mosher.mine.a2diskedit;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

public class CatEntry implements TreeNode
{
	private String sName;

	public CatEntry(byte[] rb)
	{
		byte[] rname = new byte[0x1e];
		System.arraycopy(rb,3,rname,0,0x1e);
		sName = A2DiskContents.dosName(rname);
	}

	public String getName()
	{
		return sName;
	}
	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#getIcon()
	 */
	public Icon getIcon()
	{
		return null;
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#getRightPane()
	 */
	public Component getRightPane()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<html>Catalog entry: <b>");
		sb.append(sName);
		sb.append("</b></html>");
		viewRight.setText(sb.toString());
		return viewRight;
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#setNode(DefaultMutableTreeNode)
	 */
	public void setNode(DefaultMutableTreeNode n)
	{
	}
}
