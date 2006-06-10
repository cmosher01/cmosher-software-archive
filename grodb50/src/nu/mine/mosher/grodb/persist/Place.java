/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.PlaceID;
import nu.mine.mosher.grodb.persist.types.LatLong;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Place
{
	@PrimaryKey
	private final PlaceID id;
	private final String nameShort;
	private final String nameFull;
	private final String nameAbbreviation;
	private final String nameDisplay;
	private final String notes;
	private final LatLong latlong;

	private Place()
	{
		this.id = null;
		this.nameAbbreviation = null;
		this.nameDisplay = null;
		this.nameFull = null;
		this.nameShort =null;
		this.notes = null;
		this.latlong = null;
	}
	/**
	 * @param id 
	 * @param abbreviation
	 * @param display
	 * @param full
	 * @param shortName
	 * @param notes
	 * @param latlong
	 */
	public Place(final PlaceID id, final String full, final String shortName, final String display, final String abbreviation, final String notes, final LatLong latlong)
	{
		this.id = id;
		this.nameAbbreviation = abbreviation;
		this.nameDisplay = display;
		this.nameFull = full;
		this.nameShort = shortName;
		this.notes = notes;
		this.latlong = latlong;
	}

	/**
	 * @return the ID (primary key)
	 */
	public PlaceID getID()
	{
		return this.id;
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
