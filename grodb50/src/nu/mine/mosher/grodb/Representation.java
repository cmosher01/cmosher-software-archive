package nu.mine.mosher.grodb;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class Representation
{
	private String transcript;
	private String notes;

	private String mimeType;
	private Object other;

	private List<Relation<Source,Representation>> rSource = new ArrayList<Relation<Source,Representation>>();

	public Representation()
	{
	}
}
