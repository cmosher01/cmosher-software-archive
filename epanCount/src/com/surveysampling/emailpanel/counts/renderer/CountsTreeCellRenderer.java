/*
 * Created on Sep 30, 2005
 *
 */
package com.surveysampling.emailpanel.counts.renderer;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author james
 * 
 */
public class CountsTreeCellRenderer extends DefaultTreeCellRenderer
{
	public CountsTreeCellRenderer() 
	{
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) 
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (leaf && isFolder(value)) {
			setIcon(getClosedIcon());
		}
		return this;
	}

	/**
	 * Checks if the value is a folder of type
	 * "today", "recent", or "old"
	 * 
	 * @param value
	 * @return	true, if value is a folder
	 * 			false, otherwise
	 */
	private boolean isFolder(Object value) 
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		return (node.toString().equals("today")
				||node.toString().equals("recent")
				||node.toString().equals("old"));
	}
}