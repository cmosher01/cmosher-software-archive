package nu.mosher.mine.a2diskedit;

import java.awt.Component;

import javax.swing.tree.DefaultMutableTreeNode;

public class CommandOpen extends Command
{
	public CommandOpen()
	{
		super("open");
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.Command#dispatch()
	 */
	public void dispatch()
	{
		A2DiskEdit.getApp().fileOpen();
	}

	public Component getRightPane()
	{
		return null;
	}
	/**
	 * @see nu.mosher.mine.a2diskedit.TreeNode#setNode(DefaultMutableTreeNode)
	 */
	public void setNode(DefaultMutableTreeNode n)
	{
	}

}
