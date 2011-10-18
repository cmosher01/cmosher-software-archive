package nu.mine.mosher.util;



import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;



/**
 * @author christopher_mosher
 *
 */
public class TreeNodeTest
{
	@Test
	public void nominalChildCount()
	{
		final TreeNode<String> parent = new TreeNode<String>("parent");

		final TreeNode<String> child0 = new TreeNode<String>("child0");
		parent.addChild(child0);

		final TreeNode<String> child1 = new TreeNode<String>("child1");
		parent.addChild(child1);

		assertThat(parent.getChildCount(),is(2));
	}

	@Test
	public void zeroChildCount()
	{
		final TreeNode<String> parent = new TreeNode<String>("parent");
		assertThat(parent.getChildCount(),is(0));
	}

	@Test
	public void nominalParent()
	{
		final TreeNode<String> parent = new TreeNode<String>("parent");

		final TreeNode<String> child0 = new TreeNode<String>("child0");
		parent.addChild(child0);
		assertThat(child0.parent(),sameInstance(parent));

		final TreeNode<String> child1 = new TreeNode<String>("child1");
		parent.addChild(child1);
		assertThat(child1.parent(),sameInstance(parent));
	}

	@Test
	public void nominalChildIterator()
	{
		final TreeNode<String> parent = new TreeNode<String>("parent");

		final TreeNode<String> child0 = new TreeNode<String>("child0");
		parent.addChild(child0);

		final TreeNode<String> child1 = new TreeNode<String>("child1");
		parent.addChild(child1);

		final Iterator<TreeNode<String>> i = parent.iterator();

		assertTrue(i.hasNext());
		final TreeNode<String> actualChild0 = i.next();
		assertThat(actualChild0,sameInstance(child0));

		assertTrue(i.hasNext());
		final TreeNode<String> actualChild1 = i.next();
		assertThat(actualChild1,sameInstance(child1));

		assertFalse(i.hasNext());
	}

	@Test
	public void nominalRemoveFromParent()
	{
		final TreeNode<String> parent = new TreeNode<String>("parent");

		final TreeNode<String> child0 = new TreeNode<String>("child0");
		parent.addChild(child0);

		final TreeNode<String> child1 = new TreeNode<String>("child1");
		parent.addChild(child1);

		assertThat(parent.getChildCount(),is(2));
		child0.removeFromParent();
		assertThat(parent.getChildCount(),is(1));

		final Iterator<TreeNode<String>> i = parent.iterator();
		assertTrue(i.hasNext());
		final TreeNode<String> actualChild1 = i.next();
		assertThat(actualChild1,sameInstance(child1));

		assertFalse(i.hasNext());
	}
}
