package nu.mine.mosher.grodb;

import java.io.Serializable;

public class RelationType implements Serializable
{
	private final String name;

	protected RelationType(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
