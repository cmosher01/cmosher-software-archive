package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;

public class Source
{
	private String title;
	private String author;
	private DateRange dateWritten;
	private Place placeWritten;
	private String publication;
	private DateRange dateTopicStart;
	private DateRange dateTopicEnd;
	private Place placeTopic;

	private List<Relation<Source,Source>> rRel = new ArrayList<Relation<Source,Source>>();
	private List<Relation<Source,Representation>> rRep = new ArrayList<Relation<Source,Representation>>();
	private List<Relation<Source,Search>> rSearch = new ArrayList<Relation<Source,Search>>();

	public Source()
	{
	}

	public void addRel(Source that, String description)
	{
		rRel.add(new Relation<Source,Source>(this,that,description));
	}
}
