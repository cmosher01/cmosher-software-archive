package nu.mine.mosher.gedcom;

public class GedcomTree
{
	private int prevLevel = -1;
	private TreeNode currNode = new TreeNode(null);

	public void appendLine(GedcomLine line) throws InvalidLevel
	{
		int cPops = prevLevel+1-line.getLevel();
		if (cPops < 0)
		{
			throw new InvalidLevel(line);
		}

		
	}
}
