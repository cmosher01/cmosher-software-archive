/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

import nu.mine.mosher.gedcom.GedcomTag;

public enum EventType
{
	other,
	birth,
	death,
	wedding,
	census,
	residence,
	;

	public static EventType fromGedcom(final GedcomTag tagEvent)
	{
		if (!GedcomTag.setIndividualEvent.contains(tagEvent) && !GedcomTag.setFamilyEvent.contains(tagEvent))
		{
			throw new IllegalArgumentException();
		}

		switch (tagEvent)
		{
			case BIRT: return birth;
			case DEAT: return death;
			case MARR: return wedding;
			case CENS: return census;
			case RESI: return residence;
			default: return other;
		}
	}
}
