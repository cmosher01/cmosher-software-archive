package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;

public class Place
{
	private String nameDisplay;
	private String nameFull;

	private List<Relation<Place,Place>> rPlace = new ArrayList<Relation<Place,Place>>();

	public Place()
	{
	}
}
