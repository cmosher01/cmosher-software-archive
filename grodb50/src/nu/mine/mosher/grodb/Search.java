package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;

import nu.mine.mosher.grodb.ui.ListEntry;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class Search
{
	private String description;
	private boolean completed;

	private List<Relation<Source,Search,SourceSearchRelType>> rSource = new ArrayList<Relation<Source,Search,SourceSearchRelType>>();
	private List<Relation<Hypothesis,Search,HypoSearchRelType>> rHypothesis = new ArrayList<Relation<Hypothesis,Search,HypoSearchRelType>>();

	public Search()
	{
	}

	public static String getListHeader()
	{
		List<ListEntry> rCol = new ArrayList<ListEntry>();
		rCol.add(new ListEntry("done"));
		rCol.add(new ListEntry("search description"));
		return ListEntry.formatListHeader(rCol);
	}

	public String getListEntry()
	{
		List<ListEntry> rCol = new ArrayList<ListEntry>();
		rCol.add(new ListEntry(completed?"x":""));
		rCol.add(new ListEntry(description));
		return ListEntry.formatListEntry(rCol);
	}
}
