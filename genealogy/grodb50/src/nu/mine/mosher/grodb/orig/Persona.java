package nu.mine.mosher.grodb.orig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class Persona
{
	private String nameDisplay;
	private String nameLast;
	private String nameFirst;
	private String nameOther;
	private String nameLater;

	private List<Relation<Persona,Persona,PersonaRelType>> rPersonaUp = new ArrayList<Relation<Persona,Persona,PersonaRelType>>();
	private List<Relation<Persona,Persona,PersonaRelType>> rPersonaDown = new ArrayList<Relation<Persona,Persona,PersonaRelType>>();
	
	public Persona()
	{
	}
}
