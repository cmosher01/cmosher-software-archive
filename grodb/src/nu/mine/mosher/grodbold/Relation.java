package nu.mine.mosher.grodb;

public class Relation
{
	private final Object a;
	private final Object b;
	private final String description;
	private final RelationType type;

	public Relation(Object a, Object b, RelationType type)
	{
		this(a,b,"",type);
	}

	public Relation(Object a, Object b, String description)
	{
		this(a,b,description,null);
	}

	public Relation(Object a, Object b, String description, RelationType type)
	{
		this.a = a;
		this.b = b;
		this.description = description;
		this.type = type;
	}
}
