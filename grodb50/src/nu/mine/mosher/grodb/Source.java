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

	private List<RelationSource> rRel = new ArrayList<RelationSource>();
	private List<Representation> rRep = new ArrayList<Representation>();
	private List<Search> rSearch = new ArrayList<Search>();

	public Source()
	{
	}

	public void addRel(Source that, RelationTypeSource relThisToThat)
	{
//		rRel.add(new RelationSource(that,relThisToThat));
	}
}
