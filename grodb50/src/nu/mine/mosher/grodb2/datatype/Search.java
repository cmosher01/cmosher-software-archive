/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class Search
{
	private final String notes;
	private final SourceID source; // source to search in, or source searched, if any
	private final AssertionID assertion; // assertion that lead to this search, if any

	/**
	 * @param notes
	 * @param source
	 * @param assertion
	 */
	public Search(final String notes, final SourceID source, final AssertionID assertion)
	{
		this.notes = notes;
		this.source = source;
		this.assertion = assertion;
	}

	/**
	 * @return Returns the notes.
	 */
	public String getNotes()
	{
		return this.notes;
	}

	/**
	 * @return Returns the source.
	 */
	public SourceID getSource()
	{
		return this.source;
	}

	/**
	 * @return Returns the assertion.
	 */
	public AssertionID getAssertion()
	{
		return this.assertion;
	}
}
