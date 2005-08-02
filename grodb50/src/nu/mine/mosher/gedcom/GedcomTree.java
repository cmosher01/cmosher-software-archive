package nu.mine.mosher.gedcom;

import java.util.HashMap;
import java.util.Map;

import nu.mine.mosher.util.TreeNode;

public class GedcomTree
{
	private TreeNode<GedcomLine> root;
	private Map<String,TreeNode<GedcomLine>> mapIDtoNode = new HashMap<String,TreeNode<GedcomLine>>();

	private int prevLevel;
	private TreeNode<GedcomLine> prevNode;

	public GedcomTree()
	{
		root = new TreeNode<GedcomLine>();
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

		TreeNode<GedcomLine> parent = prevNode;
		for (int i = 0; i < cPops; ++i)
        {
            parent = parent.parent();
        }

		prevLevel = line.getLevel();
		prevNode = new TreeNode<GedcomLine>(line);
		parent.addChild(prevNode);

		if (line.hasID())
		{
			mapIDtoNode.put(line.getId(),prevNode);
		}
	}

	public TreeNode<GedcomLine> getNode(String id)
	{
		return mapIDtoNode.get(id);
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(1024);
		root.appendStringDeep(sb);

		sb.append("--------map-of-IDs-to-Nodes--------\n");
		for (Map.Entry<String,TreeNode<GedcomLine>> entry : mapIDtoNode.entrySet())
        {
            sb.append(entry.getKey());
            sb.append(" --> ");
            entry.getValue().appendStringShallow(sb);
			sb.append("\n");
        }

        return sb.toString();
	}

	public TreeNode<GedcomLine> getRoot()
	{
		return root;
	}
}
