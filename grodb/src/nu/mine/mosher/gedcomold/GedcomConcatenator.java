package nu.mine.mosher.gedcomold;

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

	private void concatenateHelper(TreeNode parent)
	{
		GedcomLine parentLine = (GedcomLine)parent.getObject();
		for (Iterator i = parent.children(); i.hasNext();)
        {
            TreeNode child = (TreeNode)i.next();
            GedcomLine line = (GedcomLine)child.getObject();
            String tag = line.getTag();
            boolean cont = tag.equalsIgnoreCase("cont");
			boolean conc = tag.equalsIgnoreCase("conc");
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
