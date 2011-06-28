package nu.mine.mosher.grodbold;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RelationTypeSource implements Serializable
{
	private final int id;
	private transient final String name;

	private RelationTypeSource(String name)
	{
		this.id = enumFactory.size();
		this.name = name;

		enumFactory.add(this);
	}

	public String toString()
	{
		return name;
	}

	private static final List enumFactory = new ArrayList();

	public static final RelationTypeSource CITES = new RelationTypeSource("cites");
	public static final RelationTypeSource CONTAINS = new RelationTypeSource("contains");

	private Object readResolve() throws ObjectStreamException
	{
		return enumFactory.get(id);
	}
}
