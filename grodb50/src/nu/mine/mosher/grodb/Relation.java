package nu.mine.mosher.grodb;

import java.io.Serializable;

public class Relation<T,U> implements Serializable
{
	private final T a;
	private final U b;
	private final String description;

	public Relation(T a, U b)
	{
		this(a,b,"");
	}

	public Relation(T a, U b, String description)
	{
		this.a = a;
		this.b = b;
		this.description = description;
	}
}
