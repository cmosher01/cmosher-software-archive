package nu.mine.mosher.gedcom;

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
	}
}
