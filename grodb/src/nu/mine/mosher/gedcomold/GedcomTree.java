package nu.mine.mosher.gedcom;

public class GedcomTree
{
	private int prevLevel = -1;

	public void appendLine(GedcomLine line) throws IllegalLevelChange
	{
		if (prevLevel+1 < line.getLevel())
		{
			throw new IllegalLevelChange(line);
		}
	}
}
