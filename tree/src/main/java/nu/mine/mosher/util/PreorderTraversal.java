/*
 * Created on Jun 13, 2004
 */
package nu.mine.mosher.util;

import java.util.Iterator;

/**
 * @author Chris Mosher
 */
public class PreorderTraversal<T>
{
	@SuppressWarnings("synthetic-access")
	public static <T> void traverse(TreeNode<T> node)
	{
		Traversable tr = (Traversable)node.getObject();
		if (tr == null)
		{
			tr = new NullTraversable();
		}

		tr.enter();
		for (Iterator<TreeNode<T>> children = node.children(); children.hasNext();)
		{
			traverse(children.next());
		}
		tr.leave();
	}

	private static class NullTraversable implements Traversable
	{
        @Override
		public void enter() { /* default is no-op */ }
        @Override
		public void leave() { /* default is no-op */ }
	}
}
