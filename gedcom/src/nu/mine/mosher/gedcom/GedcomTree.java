package nu.mine.mosher.gedcom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.mine.mosher.util.TreeNode;

public class GedcomTree
{
	private final TreeNode<GedcomLine> root;
	private final Map<String,TreeNode<GedcomLine>> mapIDtoNode = new HashMap<String,TreeNode<GedcomLine>>();

	private int prevLevel;
	private TreeNode<GedcomLine> prevNode;



	public GedcomTree()
	{
		this.root = new TreeNode<GedcomLine>();
		this.prevNode = this.root;
		this.prevLevel = -1;
	}



    public TreeNode<GedcomLine> getRoot()
    {
    	return this.root;
    }

    public TreeNode<GedcomLine> getNode(final String id)
	{
		return this.mapIDtoNode.get(id);
	}



    public void appendLine(final GedcomLine line) throws InvalidLevel
	{
		final int cPops = this.prevLevel+1-line.getLevel();
		if (cPops < 0)
		{
			throw new InvalidLevel(line);
		}

		TreeNode<GedcomLine> parent = this.prevNode;
		for (int i = 0; i < cPops; ++i)
        {
            parent = parent.parent();
        }

		this.prevLevel = line.getLevel();
		this.prevNode = new TreeNode<GedcomLine>(line);
		parent.addChild(this.prevNode);

		if (line.hasID())
		{
			this.mapIDtoNode.put(line.getId(),this.prevNode);
		}
	}



    public void concatenate()
	{
		concatenateHelper(this.root);
	}

	private static void concatenateHelper(final TreeNode<GedcomLine> parent)
	{
		final GedcomLine parentLine = parent.getObject();
		for (final Iterator<TreeNode<GedcomLine>> i = parent.children(); i.hasNext();)
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



	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder(1024);
		this.root.appendStringDeep(sb);

		sb.append("--------map-of-IDs-to-Nodes--------\n");
		for (final Map.Entry<String,TreeNode<GedcomLine>> entry : this.mapIDtoNode.entrySet())
        {
            sb.append(entry.getKey().toString());
            sb.append(" --> ");
            entry.getValue().appendStringShallow(sb);
			sb.append("\n");
        }

        return sb.toString();
	}
}
