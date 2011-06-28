package nu.mine.mosher.gedcomold;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.mine.mosher.util.TreeNode;

public class GedcomTree
{
	private TreeNode root;
	private Map/*<String,TreeNode>*/ mapIDtoNode = new HashMap();

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
		return root;
	}
}
