package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class Search
{
	private String description;

	private List<Relation<Source,Search>> rSource = new ArrayList<Relation<Source,Search>>();

	public Search()
	{
	}
}
