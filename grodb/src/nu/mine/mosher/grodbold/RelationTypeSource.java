package nu.mine.mosher.grodb;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

public class RelationTypeSource
{
	private final int id;

	private final String name;

	protected RelationTypeSource(String name)
	{
		this.id = PRIVATE_VALUES.size();
		this.name = name;

		PRIVATE_VALUES.add(this);
	}

	public String toString()
	{
		return name;
	}

	private static final List PRIVATE_VALUES = new ArrayList();

	public static final RelationTypeSource CITES = new RelationTypeSource("cites");
	public static final RelationTypeSource CONTAINS = new RelationTypeSource("contains");

	private Object readResolve() throws ObjectStreamException
	{
		return PRIVATE_VALUES.get(id);
	}
}
