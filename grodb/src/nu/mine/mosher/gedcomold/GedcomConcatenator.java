package nu.mine.mosher.gedcom;

import java.util.Iterator;

import nu.mine.mosher.util.TreeNode;

public class GedcomConcatenator
{
	private final GedcomTree tree;

	public GedcomConcatenator(GedcomTree tree)
	{
		this.tree = tree;
	}

	public void concatenate()
	{
		TreeNode node = tree.getRoot();
		for (Iterator i = node.children(); i.hasNext();)
        {
            TreeNode child = (TreeNode)i.next();
            
        }
	}
}
