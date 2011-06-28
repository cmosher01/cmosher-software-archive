/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class Place
{
	private final String nameShort;
	private final String nameFull;
	private final String nameAbbreviation;
	private final String nameDisplay;
	private final String notes;
	private final LatLong latlong;

	/**
	 * @param abbreviation
	 * @param display
	 * @param full
	 * @param shortName
	 * @param notes
	 * @param latlong
	 */
	public Place(final String full, final String shortName, final String display, final String abbreviation, final String notes, final LatLong latlong)
	{
		this.nameAbbreviation = abbreviation;
		this.nameDisplay = display;
		this.nameFull = full;
		this.nameShort = shortName;
		this.notes = notes;
		this.latlong = latlong;
	}

	/**
	 * @return Returns the latlong.
	 */
	public LatLong getLatLong()
	{
		return this.latlong;
	}

	/**
	 * @return Returns the nameAbbreviation.
	 */
	public String getNameAbbreviation()
	{
		return this.nameAbbreviation;
	}

	/**
	 * @return Returns the nameDisplay.
	 */
	public String getNameDisplay()
	{
		return this.nameDisplay;
	}

	/**
	 * @return Returns the nameFull.
	 */
	public String getNameFull()
	{
		return this.nameFull;
	}

	/**
	 * @return Returns the nameShort.
	 */
	public String getNameShort()
	{
		return this.nameShort;
	}

	/**
	 * @return Returns the notes.
	 */
	public String getNotes()
	{
		return this.notes;
	}

}
