package nu.mine.mosher.util;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class TreeNode
{
	private Object object;
	private TreeNode parent;
	private List/*<TreeNode>*/ children = new ArrayList();

	public TreeNode()
	{
		this(null);
	}

	public TreeNode(Object obj)
	{
		this.object = obj;
	}

	public Object getObject()
	{
		return object;
	}

	public void setObject(Object obj)
	{
		this.object = obj;
	}

	public void addChild(TreeNode child)
	{
		if (child.parent != null)
			child.removeFromParent();

		children.add(child);
		child.parent = this;
	}

	public void removeChild(TreeNode child)
	{
		if (child.parent != this)
			throw new IllegalArgumentException("given TreeNode is not a child of this TreeNode");

		for (Iterator i = children(); i.hasNext();)
        {
            TreeNode childN = (TreeNode)i.next();
            if (childN==child)
            {
            	i.remove();
				child.parent = null;
			}
        }
	}

	public void removeFromParent()
	{
		if (parent == null)
			return;

		parent.removeChild(this);
	}

	public Iterator/*<TreeNode>*/ children()
	{
		return children.iterator();
	}

	public TreeNode parent()
	{
		return parent;
	}

	public int getChildCount()
	{
		return children.size();
	}

	public void appendStringDeep(StringBuffer sb, int level)
	{
		for (int i = 0; i < level; ++i)
        {
			sb.append("    ");
        }

		appendStringShallow(sb);
		sb.append("\n");

		++level;

		for (Iterator i = children(); i.hasNext();)
        {
            TreeNode child = (TreeNode)i.next();
            child.appendStringDeep(sb,level);
        }
	}

	public void appendStringDeep(StringBuffer sb)
	{
		appendStringDeep(sb,0);
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		appendStringDeep(sb);
		return sb.toString();
	}

	public String toStringShallow()
	{
		StringBuffer sb = new StringBuffer();
		appendStringShallow(sb);
		return sb.toString();
	}

	public void appendStringShallow(StringBuffer sb)
	{
		if (object != null)
		{
			sb.append(object.toString());
		}
		else
		{
			sb.append("[null]");
		}
	}
}
