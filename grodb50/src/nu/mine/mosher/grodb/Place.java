package nu.mine.mosher.grodb;

public class Place
{
	private String nameDisplay;
	private String nameFull;

	private List<Relation<Place,Place>> rPlace = new ArrayList<Relation<Place,Place>>();

	public Place()
	{
	}
}
