package nu.mine.mosher.util;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class GraphNode<T>
{
	private T object;
	private List<TreeNode<T>> parents = new ArrayList<TreeNode<T>>();
	private List<TreeNode<T>> children = new ArrayList<TreeNode<T>>();

	public GraphNode(T object)
	{
		if (object == null)
		{
			throw new IllegalArgumentException("GraphNode object cannot be null");
		}
		this.object = object;
	}

	public T getObject()
	{
		return object;
	}

	public void setObject(T object)
	{
		this.object = object;
	}

	public void addChild(TreeNode<T> child)
	{
		child.removeFromParent();
		children.add(child);
		child.parent = this;
	}

	public void removeChild(TreeNode<T> child)
	{
		if (child.parent != this)
		{
			throw new IllegalArgumentException("given TreeNode is not a child of this TreeNode");
		}

		for (Iterator<TreeNode<T>> i = children(); i.hasNext();)
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

	public Iterator<TreeNode<T>> children()
	{
		return children.iterator();
	}

	public TreeNode<T> parent()
	{
		return parent;
	}

	public int getChildCount()
	{
		return children.size();
	}

	protected void appendStringDeep(StringBuffer sb, int level)
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
