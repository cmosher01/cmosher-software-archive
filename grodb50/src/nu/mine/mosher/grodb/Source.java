package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;
import nu.mine.mosher.grodb.date.DateRange;

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

	private List<Relation<Source,Source,SourceRelType>> rRelUp = new ArrayList<Relation<Source,Source,SourceRelType>>();
	private List<Relation<Source,Source,SourceRelType>> rRelDown = new ArrayList<Relation<Source,Source,SourceRelType>>();
	private List<Relation<Source,Representation,SourceRepRelType>> rRep = new ArrayList<Relation<Source,Representation,SourceRepRelType>>();
	private List<Relation<Source,Search,SourceSearchRelType>> rSearch = new ArrayList<Relation<Source,Search,SourceSearchRelType>>();

	public Source()
	{
	}

	public void addRel(Source that, String descriptionThisToThat)
	{
		rRelUp.add(new Relation<Source,Source,SourceRelType>(this,that,descriptionThisToThat));
	}
}
