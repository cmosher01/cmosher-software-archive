package nu.mine.mosher.gedcom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.mine.mosher.util.TreeNode;

public class GedcomTree
{
	private TreeNode root;
	private Map<String,TreeNode> mapIDtoNode = new HashMap<String,TreeNode>();

	private int prevLevel;
	private TreeNode prevNode;

	public GedcomTree()
	{
		root = new TreeNode();
		prevNode = root;
		prevLevel = -1;
	}

	public void appendLine(GedcomLine line) throws InvalidLevel
	{
		int cPops = prevLevel+1-line.getLevel();
		if (cPops < 0)
		{
			throw new InvalidLevel(line);
		}

		TreeNode parent = prevNode;
		for (int i = 0; i < cPops; ++i)
        {
            parent = parent.parent();
        }

		prevLevel = line.getLevel();
		prevNode = new TreeNode(line);
		parent.addChild(prevNode);

		if (line.hasID())
		{
			mapIDtoNode.put(line.getId(),prevNode);
		}
	}

	public void concatenate()
	{
		concatenateHelper(root);
	}

	protected void concatenateHelper(TreeNode parent)
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

	public TreeNode getNode(String id)
	{
		return (TreeNode)mapIDtoNode.get(id);
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(1024);
		root.appendStringDeep(sb);

		sb.append("--------map-of-IDs-to-Nodes--------\n");
		for (Iterator i = mapIDtoNode.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry)i.next();
            sb.append(entry.getKey().toString());
            sb.append(" --> ");
            ((TreeNode)entry.getValue()).appendStringShallow(sb);
			sb.append("\n");
        }

        return sb.toString();
	}

    public TreeNode getRoot()
    {
    	return this.root;
    }
}
