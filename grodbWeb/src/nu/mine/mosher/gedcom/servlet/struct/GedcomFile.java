package nu.mine.mosher.gedcom.servlet.struct;
/*
 * Created on 2006-10-13.
 */
public class GedcomFile
{
	private final String fileName;
	private final String firstID;
	private final String description;
	/**
	 * @param fileName
	 * @param firstID
	 * @param description
	 */
	public GedcomFile(final String fileName, final String firstID, final String description)
	{
		this.fileName = fileName;
		this.firstID = firstID;
		this.description = description;
	}
	public String getFile()
	{
		return this.fileName;
	}
	public String getFirstID()
	{
		return this.firstID;
	}
	public String getDescription()
	{
		return this.description;
	}
}
