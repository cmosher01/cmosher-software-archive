package nu.mine.mosher.grodb;

import java.io.Serializable;

public class Relation<T,U,K> implements Serializable
{
	private final T a;
	private final U b;
	private final ItemType<K> type;

	public Relation(T a, U b, K type)
	{
		this.a = a;
		this.b = b;
		this.type = new ItemType<K>(type);
	}

	public Relation(T a, U b, String other)
	{
		this.a = a;
		this.b = b;
		this.type = new ItemType<K>(other);
	}
}
