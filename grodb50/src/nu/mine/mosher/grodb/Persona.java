package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class Persona
{
	private String nameLast;
	private String nameFirst;
	private String nameOther;
	private String nameLater;

	private List<Relation<Persona,Persona>> rPersona = new ArrayList<Relation<Persona,Persona>>();

	public Persona()
	{
	}
}
