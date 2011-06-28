/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.grodb2.datatype;

//import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.time.Time;



/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Source
{
	private final String title;
	private final String author;

	private final Time dateWritten;
	private final String placeWritten;
	private final String publication;

//	private final DatePeriod dateTopic;
//	private final Place placeTopic;



	/**
	 * @param title
	 * @param author
	 * @param dateWritten
	 * @param placeWritten
	 * @param publication
	 */
	public Source(
		final String title,
		final String author,
		final Time dateWritten,
		final String placeWritten,
		final String publication)
//		final DatePeriod dateTopic)
//		final Place placeTopic,
	{
		this.title = title;
		this.author = author;
		this.dateWritten = dateWritten;
		this.placeWritten = placeWritten;
		this.publication = publication;
//		this.dateTopic = dateTopic;
//		this.placeTopic = placeTopic;

		if (this.title == null)
		{
			throw new IllegalArgumentException();
		}
		if (this.author == null)
		{
			throw new IllegalArgumentException();
		}
		if (this.dateWritten == null)
		{
			throw new IllegalArgumentException();
		}
		if (this.placeWritten == null)
		{
			throw new IllegalArgumentException();
		}
		if (this.publication == null)
		{
			throw new IllegalArgumentException();
		}
//		if (this.dateTopic == null)
//		{
//			throw new IllegalArgumentException();
//		}
	}
//
//	public DatabaseEntry asEntry()
//	{
//		final DatabaseEntry entry = new DatabaseEntry();
//		binding.objectToEntry(this,entry);
//		return entry;
//	}
//
//	public static Source createFromEntry(final DatabaseEntry entry)
//	{
//		return (Source)binding.entryToObject(entry);
//	}



	/**
	 * @return Returns the author.
	 */
	public String getAuthor()
	{
		return this.author;
	}

//	/**
//	 * @return Returns the dateTopic.
//	 */
//	public DatePeriod getDateTopic()
//	{
//		return this.dateTopic;
//	}
//
	/**
	 * @return Returns the dateWritten.
	 */
	public Time getDateWritten()
	{
		return this.dateWritten;
	}

	/**
	 * @return Returns the placeWritten.
	 */
	public String getPlaceWritten()
	{
		return this.placeWritten;
	}

	/**
	 * @return Returns the publication.
	 */
	public String getPublication()
	{
		return this.publication;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle()
	{
		return this.title;
	}

	public String toString()
	{
		return this.author+". "+this.title+". "+this.dateWritten+". "+this.placeWritten+". ("+this.publication+").";
	}
}
