package nu.mine.mosher.gedcom;

public class GedcomTree
{
	private int prevLevel = -1;

	public void appendLine(GedcomLine line)
	{
		if (prevLevel+1 < line.getLevel())
		{
			throw new IllegalLevel(line);
		}
	}
}
