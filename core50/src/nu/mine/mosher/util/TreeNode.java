package nu.mine.mosher.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A node of a tree; also represents the (sub-)tree rooted at this node.
 *
 * @author Chris Mosher
 * @param <T> Type of object this node contains
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

	public void dump(final StringBuffer sb)
	{
		dump(0,sb);
	}

	private void dump(int level, final StringBuffer sb)
	{
		for (int i = 0; i < level; ++i)
        {
			sb.append("    ");
        }

		sb.append(toString());
		sb.append("\n");

		++level;
		for (final TreeNode<T> child : this.children)
		{
			child.dump(level,sb);
		}
	}

	@Override
	public String toString()
	{
		if (this.object == null)
		{
			return "";
		}
		return this.object.toString();
	}
}
