package nu.mine.mosher.grodb;

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

	private List rRel;
	private List rRep;
	private List rSearch;

}
