package nu.mine.mosher.util;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents the node of a graph. A node can have any number
 * of "parent" nodes and any number of "child" nodes.
 * It can also have an object of type <code>T</code> associated with it.
 * 
 * @author Chris Mosher
 */
public class GraphNode<T>
{
	private final Set<GraphNode<T>> parents = new HashSet<GraphNode<T>>();
	private final Set<GraphNode<T>> children = new HashSet<GraphNode<T>>();
	private T object;



	public T getObject()
	{
		return this.object;
	}

	public void setObject(final T object)
	{
		this.object = object;
	}



	public void addChild(final GraphNode<T> child)
	{
		this.children.add(child);
		child.addParent(this);
	}

	public void removeChild(final GraphNode<T> child)
	{
		if (!child.hasParent(this))
		{
			throw new IllegalArgumentException("given GraphNode is not a child of this GraphNode");
		}
		child.parents.remove(this);
		this.children.remove(child);
	}

	public boolean hasChild(final GraphNode<T> child)
	{
		return this.children.contains(child);
	}

	public void getChildren(final Collection<GraphNode<T>> addTo)
	{
		addTo.addAll(this.children);
	}

	public int getChildCount()
	{
		return this.children.size();
	}



	public void addParent(final GraphNode<T> parent)
	{
		this.parents.add(parent);
		parent.addChild(this);
	}

	public void removeParent(final GraphNode<T> parent)
	{
		if (!parent.hasChild(this))
		{
			throw new IllegalArgumentException("given GraphNode is not a parent of this GraphNode");
		}
		parent.children.remove(this);
		this.parents.remove(parent);
	}

	public boolean hasParent(final GraphNode<T> parent)
	{
		return this.parents.contains(parent);
	}

	public void getParents(final Collection<GraphNode<T>> addTo)
	{
		addTo.addAll(this.parents);
	}

	public int getParentCount()
	{
		return this.parents.size();
	}
}
