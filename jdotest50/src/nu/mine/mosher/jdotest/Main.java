package nu.mine.mosher.jdotest;

/**
 * @author Chris Mosher
 * Created: Feb 19, 2004
 */
public class Main
{
	public static void main(String[] args)
	{
		PersonPersist p = new PersonPersist();
		p.persistPeople();
		p.display();
	}
}
