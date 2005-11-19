package nu.mine.mosher.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A node of a tree; also represents the (sub-)tree rooted at this node.
 *
 * @author Chris Mosher
 */
public class TreeNode<T>
{
	private T object;
	private TreeNode<T> parent;
	private final List<TreeNode<T>> children = new ArrayList<TreeNode<T>>();



	public T getObject()
	{
		return this.object;
	}

	public void setObject(final T obj)
	{
		this.object = obj;
	}



	public void addChild(final TreeNode<T> child)
	{
		child.removeFromParent();
		this.children.add(child);
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
		if (this.parent == null)
		{
			return;
		}

		this.parent.removeChild(this);
	}

	public void getChildren(final Collection<TreeNode<T>> addTo)
	{
		addTo.addAll(this.children);
	}

	public TreeNode<T> parent()
	{
		return this.parent;
	}

	public int getChildCount()
	{
		return this.children.size();
	}

	protected void appendStringDeep(int level, final StringBuffer sb)
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
			child.appendStringDeep(level,sb);
		}
	}

	public void appendStringDeep(final StringBuffer sb)
	{
		appendStringDeep(0,sb);
	}

	@Override
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
		if (this.object != null)
		{
			sb.append(this.object.toString());
		}
		else
		{
			sb.append("[null]");
		}
	}
}
