package nu.mine.mosher.gedcom;

import nu.mine.mosher.util.TreeNode;

public class GedcomTree
{
	private int prevLevel = -1;
	private TreeNode prevNode = new TreeNode();

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

		prevNode = new TreeNode(line);
		parent.addChild(prevNode);
	}
}
