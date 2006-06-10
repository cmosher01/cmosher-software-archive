package nu.mine.mosher.grodb.orig;

import java.util.ArrayList;
import java.util.List;

public class Place
{
	private String nameDisplay;
	private String nameFull;

	private List<Relation<Place,Place,PlaceRelType>> rPlaceUp = new ArrayList<Relation<Place,Place,PlaceRelType>>();
	private List<Relation<Place,Place,PlaceRelType>> rPlaceDown = new ArrayList<Relation<Place,Place,PlaceRelType>>();
	
	public Place()
	{
	}
}
