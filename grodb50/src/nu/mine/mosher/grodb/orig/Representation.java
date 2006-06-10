package nu.mine.mosher.grodb.orig;

import java.util.ArrayList;
import java.util.List;

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

	private List<Relation<Source,Representation,SourceRepRelType>> rSource = new ArrayList<Relation<Source,Representation,SourceRepRelType>>();

	public Representation()
	{
	}
}
