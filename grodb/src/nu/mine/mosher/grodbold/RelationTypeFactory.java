package nu.mine.mosher.grodb;

public class RelationTypeFactory
{
	private final int idNext = 0;
	private final Class cl;

	public RelationTypeFactory(Class relationType)
	{
		cl = relationType;
		assert RelationType.class.isAssignableFrom(cl);
	}

//	public RelationType make(String name)
//	{
//		return cl.newInstance();
//	}
}
