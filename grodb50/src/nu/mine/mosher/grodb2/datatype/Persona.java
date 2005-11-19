/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class Persona
{
	private final String slashedName; // "Christopher Alan /Mosher/, the Programmer"

	private final String fullName; // "Christopher Alan Mosher, the Programmer"
	private final String givenNames; // "Christopher Alan, the Programmer"
	private final String surnames; // "Mosher"
	private final String sortableName; // "Mosher, Christopher Alan, the Programmer"

	/**
	 * @param nameWithSlashedSurname
	 */
	public Persona(final String nameWithSlashedSurname)
	{
		this.slashedName = nameWithSlashedSurname;

		final int posSlashFirst = this.slashedName.indexOf('/');
		if (posSlashFirst < 0)
		{
			throw new IllegalArgumentException();
		}

		final int posSlashLast = this.slashedName.lastIndexOf('/');
		if (posSlashLast < 0)
		{
			throw new IllegalArgumentException();
		}

		if (posSlashLast == posSlashFirst)
		{
			throw new IllegalArgumentException();
		}

		// TODO fix name parsing
		this.givenNames = this.slashedName;
		this.surnames = this.slashedName;
		this.fullName = this.slashedName;
		this.sortableName = this.slashedName;
	}

	/**
	 * @return Returns the slashedName.
	 */
	public String getSlashedName()
	{
		return this.slashedName;
	}

	/**
	 * @return Returns the fullName.
	 */
	public String getFullName()
	{
		return this.fullName;
	}

	/**
	 * @return Returns the givenNames.
	 */
	public String getGivenNames()
	{
		return this.givenNames;
	}

	/**
	 * @return Returns the sortableName.
	 */
	public String getSortableName()
	{
		return this.sortableName;
	}

	/**
	 * @return Returns the surnames.
	 */
	public String getSurnames()
	{
		return this.surnames;
	}
}
