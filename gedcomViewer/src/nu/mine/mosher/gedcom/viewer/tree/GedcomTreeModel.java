/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.tree;

import java.io.BufferedReader;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import nu.mine.mosher.gedcom.Gedcom;
import nu.mine.mosher.gedcom.GedcomLine;
import nu.mine.mosher.gedcom.GedcomTree;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.util.TreeNode;



public class GedcomTreeModel extends Observable implements TreeModel, Closeable
{
	private GedcomTree tree;
    private List<TreeModelListener> rListener = new ArrayList<TreeModelListener>();


	public void open(final BufferedReader reader) throws InvalidLevel
	{
		final GedcomTree gt = Gedcom.readTree(reader);
		setTree(gt);
	}

	public void close()
	{
		setTree(null);
	}



	public Object getRoot()
	{
		if (this.tree == null)
		{
			return null;
		}

		return this.tree.getRoot();
	}

	public Object getChild(final Object parent, final int index)
	{
		if (parent == null)
		{
			return null;
		}

		final TreeNode<GedcomLine> nodeParent = (TreeNode<GedcomLine>)parent;

		final ArrayList<TreeNode<GedcomLine>> rChild = new ArrayList<TreeNode<GedcomLine>>();
		nodeParent.getChildren(rChild);

		return rChild.get(index);
	}

	public int getChildCount(final Object parent)
	{
		if (parent == null)
		{
			return 0;
		}

		final TreeNode<GedcomLine> nodeParent = (TreeNode<GedcomLine>)parent;
		return nodeParent.getChildCount();
	}

	public boolean isLeaf(final Object node)
	{
		return getChildCount(node) == 0;
	}

	public void valueForPathChanged(final TreePath path, final Object newValue)
	{
		throw new UnsupportedOperationException();
	}

	public int getIndexOfChild(final Object parent, final Object child)
	{
		if (parent == null || child == null)
		{
			return 0;
		}

		final TreeNode<GedcomLine> nodeParent = (TreeNode<GedcomLine>)parent;

		final ArrayList<TreeNode<GedcomLine>> rChild = new ArrayList<TreeNode<GedcomLine>>();
		nodeParent.getChildren(rChild);

		return rChild.indexOf(child);
	}



	public void addTreeModelListener(final TreeModelListener listener)
	{
        this.rListener.add(listener);
	}

	public void removeTreeModelListener(final TreeModelListener listener)
	{
        this.rListener.remove(listener);
	}



	private void setTree(final GedcomTree tree)
	{
		this.tree = tree;

		fireTreeStructureChanged();
		setChanged();

		notifyObservers();
	}

	private void fireTreeStructureChanged()
    {
        final TreeModelEvent eventRootChanged = new TreeModelEvent(this,getRootPath());

        for (final TreeModelListener listener : this.rListener)
		{
			listener.treeStructureChanged(eventRootChanged);
		}
    }

	private Object[] getRootPath()
	{
		final Object root;
		if (this.tree == null)
		{
			root = "";
		}
		else
		{
			root = this.tree.getRoot();
		}

		return new Object[] { root };
	}
}
