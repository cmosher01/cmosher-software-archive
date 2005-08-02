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
		concatenateHelper(tree.getRoot());
	}

	private void concatenateHelper(final TreeNode<GedcomLine> parent)
	{
		final GedcomLine parentLine = parent.getObject();

		for (final Iterator<TreeNode<GedcomLine>> i = parent.children().iterator(); i.hasNext();)
        {
            final TreeNode<GedcomLine> child = i.next();

            final GedcomLine line = child.getObject();

            final String tag = line.getTag();
            final boolean cont = tag.equalsIgnoreCase("cont");
			final boolean conc = tag.equalsIgnoreCase("conc");

			if (cont)
            {
            	parentLine.contValue(line.getValue());
            	i.remove();
            }
            else if (conc)
            {
				parentLine.concValue(line.getValue());
				i.remove();
            }
            else
            {
				concatenateHelper(child);
            }
        }
	}
}
