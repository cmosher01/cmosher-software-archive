package nu.mine.mosher.grodbold;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class RelationType implements Serializable
{
	private static int idNext = 0;
	private final int id = idNext++;

	private final String name;

	protected RelationType(String name)
	{
		this.name = name;
	}

    public String toString()
    {
        return name;
    }

    public static final RelationType UNKNOWN = new RelationType("unknown relation");

	private static final RelationType[] PRIVATE_VALUES = { UNKNOWN };

	private Object readResolve() throws ObjectStreamException
	{
		return PRIVATE_VALUES[id]; // Canonicalize
	}
}
