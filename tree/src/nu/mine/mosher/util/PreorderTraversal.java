/*
 * Created on Jun 13, 2004
 */
package nu.mine.mosher.util;

import java.util.Iterator;

/**
 * @author Chris Mosher
 */
public class PreorderTraversal
{
	public static void traverse(TreeNode node)
	{
		Traversable tr = (Traversable)node.getObject();
		if (tr == null)
		{
			tr = new NullTraversable();
		}

		tr.enter();
		for (Iterator<TreeNode> children = node.children(); children.hasNext();)
		{
			traverse(children.next());
		}
		tr.leave();
	}

	private static class NullTraversable implements Traversable
	{
        public void enter() { }
        public void leave() { }
	}
}
