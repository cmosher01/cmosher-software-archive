package nu.mine.mosher.jdotest;

/**
 * @author Chris Mosher
 * Created: Feb 18, 2004
 */
public class Person
{
	private String name;

	public Person()
	{
	}

	public Person(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
