package nu.mine.mosher.util;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class GraphNode<T>
{
	private T object;

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



	private Set<GraphNode<T>> children = new HashSet<GraphNode<T>>();

	public void addChild(GraphNode<T> child)
	{
		this.children.add(child);
		child.addParent(this);
	}

	public boolean hasChild(GraphNode<T> child)
	{
		return this.children.contains(child);
	}

	public void removeChild(GraphNode<T> child)
	{
		if (!child.hasParent(this))
		{
			throw new IllegalArgumentException("given GraphNode is not a child of this GraphNode");
		}
		child.parents.remove(this);
		this.children.remove(child);
	}

	public Iterator<GraphNode<T>> children()
	{
		return children.iterator();
	}

	public int getChildCount()
	{
		return children.size();
	}



	private Set<GraphNode<T>> parents = new HashSet<GraphNode<T>>();

	public void addParent(GraphNode<T> parent)
	{
		this.parents.add(parent);
		parent.addChild(this);
	}

	public boolean hasParent(GraphNode<T> parent)
	{
		return this.parents.contains(parent);
	}

	public void removeParent(GraphNode<T> parent)
	{
		if (!parent.hasChild(this))
		{
			throw new IllegalArgumentException("given GraphNode is not a parent of this GraphNode");
		}
		parent.children.remove(this);
		this.parents.remove(parent);
	}

	public Iterator<GraphNode<T>> parents()
	{
		return parents.iterator();
	}

	public int getParentCount()
	{
		return parents.size();
	}
}
