package nu.mine.mosher.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class TreeNode<T>
{
	private T object;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children = new ArrayList<TreeNode<T>>();

	public TreeNode()
	{
		this(null);
	}

	public TreeNode(T obj)
	{
		this.object = obj;
	}

	public T getObject()
	{
		return object;
	}

	public void setObject(final T obj)
	{
		this.object = obj;
	}

	public void addChild(final TreeNode<T> child)
	{
		child.removeFromParent();
		children.add(child);
		child.parent = this;
	}

	public void removeChild(final TreeNode<T> child)
	{
		if (child.parent != this)
		{
			throw new IllegalArgumentException("given TreeNode is not a child of this TreeNode");
		}

		for (Iterator<TreeNode<T>> i = this.children.iterator(); i.hasNext();)
        {
            if (i.next()==child)
            {
            	i.remove();
				child.parent = null;
			}
        }
	}

	public void removeFromParent()
	{
		if (parent == null)
		{
			return;
		}

		parent.removeChild(this);
	}

	public List<TreeNode<T>> children()
	{
		return Collections.unmodifiableList(this.children);
	}

	public TreeNode<T> parent()
	{
		return parent;
	}

	public int getChildCount()
	{
		return children.size();
	}

	protected void appendStringDeep(final StringBuffer sb, int level)
	{
		for (int i = 0; i < level; ++i)
        {
			sb.append("    ");
        }

		appendStringShallow(sb);
		sb.append("\n");

		++level;
		for (TreeNode<T> child : this.children)
		{
			child.appendStringDeep(sb,level);
		}
	}

	public void appendStringDeep(final StringBuffer sb)
	{
		appendStringDeep(sb,0);
	}

	public String toString()
	{
		final StringBuffer sb = new StringBuffer();
		appendStringDeep(sb);
		return sb.toString();
	}

	public String toStringShallow()
	{
		final StringBuffer sb = new StringBuffer();
		appendStringShallow(sb);
		return sb.toString();
	}

	public void appendStringShallow(final StringBuffer sb)
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
