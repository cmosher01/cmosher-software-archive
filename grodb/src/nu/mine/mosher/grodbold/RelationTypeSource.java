package nu.mine.mosher.grodb;

import java.io.ObjectStreamException;

public class RelationTypeSource
{
	private final int id;

	private final String name;

	protected RelationTypeSource(String name)
	{
		this.id = PRIVATE_VALUES.length;
		this.name = name;
	}

	public String toString()
	{
		return name;
	}

	public static final RelationTypeSource CITES = new RelationTypeSource("cites");
	public static final RelationTypeSource CONTAINS = new RelationTypeSource("contains");

	private static final RelationTypeSource[] PRIVATE_VALUES = { CITES, CONTAINS };

	private Object readResolve() throws ObjectStreamException
	{
		return PRIVATE_VALUES[id];
	}
}
