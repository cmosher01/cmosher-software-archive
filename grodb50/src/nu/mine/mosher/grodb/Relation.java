package nu.mine.mosher.grodb;

import java.io.Serializable;

public class Relation<T,U,K> implements Serializable
{
	private final T a;
	private final U b;
	private final ItemType<K> type;

	public Relation(T a, U b, ItemType<K> type)
	{
		this.a = a;
		this.b = b;
		this.type = type;
	}
}
