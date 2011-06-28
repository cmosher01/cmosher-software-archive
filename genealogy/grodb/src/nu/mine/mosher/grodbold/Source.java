package nu.mine.mosher.grodbold;

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

	private List rRel = new ArrayList();
	private List rRep = new ArrayList();
	private List rSearch = new ArrayList();

	public Source()
	{
	}

	public void addRel(Source that, RelationTypeSource relThisToThat)
	{
//		rRel.add(new RelationSource(that,relThisToThat));
	}
}
