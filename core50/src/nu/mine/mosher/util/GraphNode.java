package nu.mine.mosher.util;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class GraphNode<T>
{
	private T object;
	private List<GraphNode<T>> parents = new ArrayList<GraphNode<T>>();
	private List<GraphNode<T>> children = new ArrayList<GraphNode<T>>();

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

	public void addChild(GraphNode<T> child)
	{
		this.children.add(child);
		child.addParent(this);
	}

	public boolean hasChild(GraphNode<T> child)
	{
		return this.children.contains(child);
	}

	public void addParent(GraphNode<T> parent)
	{
		this.parents.add(parent);
		parent.addChild(this);
	}

	public boolean hasParent(GraphNode<T> parent)
	{
		return this.parents.contains(parent);
	}

	public void removeChild(GraphNode<T> child)
	{
		if (!child.hasParent(this))
		{
			throw new IllegalArgumentException("given GraphNode is not a child of this GraphNode");
		}
		child.parents.remove(this);
		this.children.remove(child);

		for (Iterator<GraphNode<T>> i = children(); i.hasNext();)
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

	public Iterator<GraphNode<T>> children()
	{
		return children.iterator();
	}

	public GraphNode<T> parent()
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
		for (GraphNode<T> child : this.children)
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
